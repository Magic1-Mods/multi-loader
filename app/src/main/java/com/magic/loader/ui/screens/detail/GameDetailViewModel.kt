package com.magic.loader.ui.screens.detail

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.magic.loader.Main
import com.magic.loader.data.model.Game
import com.magic.loader.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.niunaijun.blackbox.BlackBoxCore
import java.io.File
import javax.inject.Inject

enum class GameStatus {
    Initial, Checking, Ready, NotInstalled, VersionMismatch, Launching, Error, NoPermissions
}

data class GameDetailUiState(
    val game: Game? = null,
    val gameName: String = "",
    val installedVersion: String? = null,
    val gameIcon: Drawable? = null,
    val permissionsGranted: Boolean = false,
    val isChecking: Boolean = false,
    val status: GameStatus = GameStatus.Initial,
    val statusMessage: String = "",
    val canStart: Boolean = false,
    val showUpdate: Boolean = false,
    val isLaunching: Boolean = false,
    val soExtracted: Boolean = false
)

@HiltViewModel
class GameDetailViewModel @Inject constructor(
    application: Application,
    private val gameRepository: GameRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(GameDetailUiState())
    val uiState: StateFlow<GameDetailUiState> = _uiState.asStateFlow()

    fun loadGame(gameName: String) {
        viewModelScope.launch {
            val game = gameRepository.getGameByName(getApplication(), gameName)
            if (game == null) {
                _uiState.value = _uiState.value.copy(
                    status = GameStatus.Error,
                    statusMessage = "Game not found in config"
                )
                return@launch
            }
            _uiState.value = _uiState.value.copy(game = game, gameName = game.name)
            checkPermissionsAndGame(getApplication())
        }
    }

    fun checkPermissionsAndGame(context: Context) {
        val granted = hasRequiredPermissions(context)
        _uiState.value = _uiState.value.copy(permissionsGranted = granted)
        if (granted) checkGameStatus(context) else {
            _uiState.value = _uiState.value.copy(
                status = GameStatus.NoPermissions,
                statusMessage = "Permissions required",
                canStart = false
            )
        }
    }

    fun onPermissionsResult(context: Context, granted: Boolean) {
        _uiState.value = _uiState.value.copy(permissionsGranted = granted)
        if (granted) checkGameStatus(context) else {
            _uiState.value = _uiState.value.copy(
                status = GameStatus.NoPermissions,
                statusMessage = "Permissions denied",
                canStart = false
            )
        }
    }

    private fun checkGameStatus(context: Context) {
        val game = _uiState.value.game ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isChecking = true,
                status = GameStatus.Checking,
                statusMessage = "Checking game\u2026"
            )
            delay(300)
            val pm = context.packageManager
            try {
                val appInfo = pm.getApplicationInfo(game.packageName, 0)
                val icon = pm.getApplicationIcon(appInfo)
                val installedVer = pm.getPackageInfo(game.packageName, 0).versionName
                val match = installedVer == game.version

                _uiState.value = _uiState.value.copy(
                    gameIcon = icon,
                    installedVersion = installedVer,
                    isChecking = false,
                    status = if (match) GameStatus.Ready else GameStatus.VersionMismatch,
                    statusMessage = if (match) "Ready to launch" else "Version mismatch",
                    canStart = match,
                    showUpdate = !match
                )
            } catch (_: PackageManager.NameNotFoundException) {
                _uiState.value = _uiState.value.copy(
                    isChecking = false,
                    status = GameStatus.NotInstalled,
                    statusMessage = "Game not installed",
                    canStart = false,
                    showUpdate = false
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error checking game", e)
                _uiState.value = _uiState.value.copy(
                    isChecking = false,
                    status = GameStatus.Error,
                    statusMessage = "Check failed",
                    canStart = false
                )
            }
        }
    }

    fun launchGame(context: Context) {
        val game = _uiState.value.game ?: return
        if (!_uiState.value.permissionsGranted) return

        _uiState.value = _uiState.value.copy(
            isLaunching = true,
            status = GameStatus.Launching,
            statusMessage = "Extracting libraries\u2026"
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val nativeDir = File(context.filesDir, "native_libs")
                if (!nativeDir.exists()) nativeDir.mkdirs()
                val soFile = gameRepository.extractLibrary(context, game, nativeDir)

                if (soFile == null) {
                    withContext(Dispatchers.Main) {
                        _uiState.value = _uiState.value.copy(
                            isLaunching = false,
                            status = GameStatus.Error,
                            statusMessage = "Library extraction failed",
                            canStart = true
                        )
                    }
                    return@launch
                }

                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        statusMessage = "Launching game\u2026",
                        soExtracted = true
                    )
                }

                Main.init(soFile.absolutePath)
                BlackBoxCore.get().installPackageAsUser(game.packageName, 0)
                BlackBoxCore.get().launchApk(game.packageName, 0)

                withContext(Dispatchers.Main) {
                    (context as? android.app.Activity)?.let { activity ->
                        android.os.Handler(android.os.Looper.getMainLooper())
                            .postDelayed({ activity.finish() }, 500)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to launch", e)
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        isLaunching = false,
                        status = GameStatus.Error,
                        statusMessage = "Launch failed: ${e.message}",
                        canStart = true
                    )
                }
            }
        }
    }

    fun openUrl(context: Context, url: String?) {
        if (url.isNullOrEmpty()) {
            android.widget.Toast.makeText(
                context, "No link available",
                android.widget.Toast.LENGTH_SHORT
            ).show()
            return
        }
        try {
            val intent = android.content.Intent(
                android.content.Intent.ACTION_VIEW,
                android.net.Uri.parse(url)
            )
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open URL", e)
            android.widget.Toast.makeText(
                context, "Cannot open link",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun hasRequiredPermissions(context: Context): Boolean {
        return when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ->
                ContextCompat.checkSelfPermission(
                    context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                ContextCompat.checkSelfPermission(
                    context, android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            else -> true
        }
    }

    companion object {
        private const val TAG = "GameDetailViewModel"
    }
}

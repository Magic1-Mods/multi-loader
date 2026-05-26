package com.magic.loader.data.repository

import android.content.Context
import android.content.pm.PackageManager
import com.magic.loader.data.model.Game
import com.magic.loader.data.model.GameConfig
import com.magic.loader.data.parser.ConfigParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

data class GameWithStatus(
    val game: Game,
    val isInstalled: Boolean = false,
    val installedVersion: String? = null,
    val versionMatch: Boolean = false
)

sealed interface GameLaunchResult {
    data object Success : GameLaunchResult
    data class Error(val message: String) : GameLaunchResult
}

@Singleton
class GameRepository @Inject constructor(
    private val configParser: ConfigParser
) {
    private var cachedGames: List<Game> = emptyList()

    suspend fun loadGames(context: Context): List<Game> = withContext(Dispatchers.IO) {
        if (cachedGames.isEmpty()) {
            cachedGames = configParser.parse(context).games
        }
        cachedGames
    }

    suspend fun getGamesWithStatus(context: Context): List<GameWithStatus> =
        withContext(Dispatchers.IO) {
            val games = loadGames(context)
            val pm = context.packageManager
            games.map { game ->
                try {
                    val pkgInfo = pm.getPackageInfo(game.packageName, 0)
                    val installedVersion = pkgInfo.versionName
                    GameWithStatus(
                        game = game,
                        isInstalled = true,
                        installedVersion = installedVersion,
                        versionMatch = installedVersion == game.version
                    )
                } catch (_: PackageManager.NameNotFoundException) {
                    GameWithStatus(game = game)
                }
            }
        }

    suspend fun getGameByName(context: Context, name: String): Game? =
        withContext(Dispatchers.IO) {
            loadGames(context).find { it.name == name }
        }

    suspend fun extractLibrary(
        context: Context,
        game: Game,
        targetDir: java.io.File
    ): java.io.File = withContext(Dispatchers.IO) {
        val soFile = java.io.File(targetDir, game.library)
        if (!soFile.exists()) {
            context.assets.open(game.library).use { input ->
                soFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            soFile.setExecutable(true)
        }
        soFile
    }
}

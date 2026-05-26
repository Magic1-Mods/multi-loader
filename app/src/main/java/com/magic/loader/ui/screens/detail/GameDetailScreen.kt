package com.magic.loader.ui.screens.detail

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.magic.loader.ui.components.GlassPanel
import com.magic.loader.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    gameName: String,
    onBack: () -> Unit,
    viewModel: GameDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        viewModel.onPermissionsResult(context, results.all { it.value })
    }

    LaunchedEffect(gameName) {
        viewModel.loadGame(gameName)
    }

    val canLaunch = uiState.canStart && !uiState.isLaunching && uiState.permissionsGranted

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.gameName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    if (!uiState.permissionsGranted) {
                        IconButton(onClick = {
                            val permissions = when {
                                Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ->
                                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                                    arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                                else -> null
                            }
                            permissions?.let { permissionLauncher.launch(it) }
                                ?: viewModel.onPermissionsResult(context, true)
                        }) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Permissions",
                                tint = WarningAmber
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))

            GameIconSection(uiState.gameIcon, uiState.gameName)

            Spacer(Modifier.height(16.dp))

            Text(
                text = uiState.gameName,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            uiState.game?.let { game ->
                Spacer(Modifier.height(4.dp))
                Text(
                    text = game.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(12.dp))

                GlassPanel(
                    shape = RoundedCornerShape(16.dp),
                    borderColor = PrimaryIndigo.copy(alpha = 0.2f),
                    gradient = Brush.linearGradient(
                        colors = listOf(GlassWhite, Color.Transparent)
                    ),
                    contentPadding = 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlassPanel(
                            shape = RoundedCornerShape(10.dp),
                            borderColor = PrimaryIndigo.copy(alpha = 0.2f),
                            gradient = Brush.linearGradient(
                                colors = listOf(PrimaryIndigo.copy(alpha = 0.12f), Color.Transparent)
                            ),
                            contentPadding = 0.dp
                        ) {
                            Text(
                                text = "Required: v${game.version}",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = PrimaryIndigo,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                        if (uiState.installedVersion != null) {
                            GlassPanel(
                                shape = RoundedCornerShape(10.dp),
                                borderColor = SecondaryCyan.copy(alpha = 0.2f),
                                gradient = Brush.linearGradient(
                                    colors = listOf(SecondaryCyan.copy(alpha = 0.1f), Color.Transparent)
                                ),
                                contentPadding = 0.dp
                            ) {
                                Text(
                                    text = "Installed: v${uiState.installedVersion}",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                    color = SecondaryCyan,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                if (game.description != null) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = game.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            StatusCard(
                status = uiState.status,
                message = uiState.statusMessage,
                isChecking = uiState.isChecking
            )

            Spacer(Modifier.height(20.dp))

            val buttonScale by animateFloatAsState(
                targetValue = if (canLaunch) 1f else 0.96f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "launch_scale"
            )

            Button(
                onClick = { viewModel.launchGame(context) },
                enabled = canLaunch,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .scale(buttonScale),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryIndigo,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (uiState.isLaunching) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(12.dp))
                    } else {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(Modifier.width(12.dp))
                    }
                    Text(
                        text = when {
                            uiState.isLaunching -> "Launching\u2026"
                            !uiState.permissionsGranted -> "Grant Permissions"
                            !uiState.status.name.contains("Ready") -> uiState.statusMessage
                            else -> "Launch Game"
                        },
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            AnimatedVisibility(
                visible = uiState.showUpdate,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                OutlinedButton(
                    onClick = { viewModel.openUrl(context, uiState.game?.updateUrl) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(top = 12.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Update Required")
                }
            }

            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            )

            Spacer(Modifier.height(16.dp))

            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                borderColor = GlassBorder,
                gradient = Brush.linearGradient(
                    colors = listOf(GlassWhite, Color.Transparent)
                ),
                contentPadding = 0.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Runtime Status",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                    Spacer(Modifier.height(10.dp))
                    RuntimeStatusRow(
                        label = "Native Library",
                        active = uiState.soExtracted
                    )
                    RuntimeStatusRow(
                        label = "Virtual Space",
                        active = uiState.permissionsGranted && uiState.status == GameStatus.Ready
                    )
                    RuntimeStatusRow(
                        label = "Game Detection",
                        active = uiState.status == GameStatus.Ready
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GameIconSection(icon: android.graphics.drawable.Drawable?, gameName: String) {
    val painter = remember(icon) {
        icon?.toBitmap()?.asImageBitmap()
            ?.let { BitmapPainter(it) }
    }

    GlassPanel(
        modifier = Modifier.size(120.dp),
        shape = RoundedCornerShape(28.dp),
        borderColor = GlassBorder.copy(alpha = 0.5f),
        contentPadding = 0.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (painter != null) {
                Image(
                    painter = painter,
                    contentDescription = gameName,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(24.dp))
                )
            } else {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
private fun StatusCard(status: GameStatus, message: String, isChecking: Boolean) {
    val accent = when (status) {
        GameStatus.Ready -> SuccessGreen
        GameStatus.Launching, GameStatus.Checking -> PrimaryIndigo
        else -> ErrorRed
    }

    GlassPanel(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        borderColor = accent.copy(alpha = 0.2f),
        gradient = Brush.linearGradient(
            colors = listOf(accent.copy(alpha = 0.06f), Color.Transparent)
        ),
        contentPadding = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                if (isChecking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(26.dp),
                        color = accent,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    val icon = when (status) {
                        GameStatus.Ready -> Icons.Default.Check
                        GameStatus.Launching -> Icons.Default.PlayArrow
                        GameStatus.Checking -> Icons.Default.Refresh
                        else -> Icons.Default.Warning
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = accent
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Crossfade(targetState = message, label = "status_text") { target ->
                Text(
                    text = target,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun RuntimeStatusRow(label: String, active: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (active) Icons.Default.CheckCircle else Icons.Default.Refresh,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = if (active) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

package com.magic.loader

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.magic.loader.ui.components.GlassPanel
import com.magic.loader.ui.theme.*

class CrashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crashLog = intent.getStringExtra("crash_text") ?: "No crash data received"

        setContent {
            MagicLoaderTheme(darkTheme = true) {
                CrashScreen(
                    crashLog = crashLog,
                    onCopy = {
                        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("crash_log", crashLog))
                        Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    onRestart = {
                        val intent = Intent(this, MainActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        }
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
private fun CrashScreen(
    crashLog: String,
    onCopy: () -> Unit,
    onRestart: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                borderColor = ErrorRed.copy(alpha = 0.3f),
                gradient = Brush.linearGradient(
                    colors = listOf(ErrorRed.copy(alpha = 0.08f), GlassWhite.copy(alpha = 0.02f))
                ),
                contentPadding = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Crash Detected",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = ErrorRed
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Copy the log and send it to the developer",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row {
                        IconButton(onClick = onCopy) {
                            Icon(Icons.Default.Share, "Copy", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }
                        IconButton(onClick = onRestart) {
                            Icon(Icons.Default.Refresh, "Restart", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            GlassPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(20.dp),
                borderColor = GlassBorder,
                gradient = Brush.linearGradient(
                    colors = listOf(GlassWhite, Color.Transparent)
                ),
                contentPadding = 0.dp
            ) {
                Text(
                    text = crashLog,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Color(0xFFCCCCDD),
                    lineHeight = 16.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onRestart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
            ) {
                Text(
                    "Restart App",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

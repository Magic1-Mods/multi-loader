package com.magic.loader.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.magic.loader.ui.theme.PrimaryIndigo
import com.magic.loader.ui.theme.SecondaryCyan

@Composable
fun AnimatedLaunchButton(
    enabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (enabled && !isLoading) 1f else 0.96f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "btn_scale"
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .scale(scale),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .scale(scale)
        )
        val brush = Brush.horizontalGradient(
            colors = listOf(PrimaryIndigo, SecondaryCyan)
        )

        Surface(
            modifier = Modifier.matchParentSize(),
            shape = RoundedCornerShape(20.dp),
            color = if (enabled) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant
        ) {
            if (enabled) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(brush, RoundedCornerShape(20.dp))
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(12.dp))
            }
            Text(
                text = if (isLoading) "Launching\u2026" else "Launch Game",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

private fun Modifier.scale(scale: Float): Modifier = this.then(
    androidx.compose.ui.draw.scale(scale)
)

private val Modifier.background: Modifier
    get() = this

package com.magic.loader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.magic.loader.ui.theme.GlassBorder
import com.magic.loader.ui.theme.GlassWhite

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    borderColor: Color = GlassBorder,
    borderWidth: Dp = 1.dp,
    gradient: Brush? = null,
    contentPadding: Dp = 0.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = gradient ?: Brush.linearGradient(
                    colors = listOf(GlassWhite, GlassWhite.copy(alpha = 0.05f))
                ),
                shape = shape
            )
            .border(borderWidth, borderColor, shape)
            .padding(contentPadding),
        content = content
    )
}

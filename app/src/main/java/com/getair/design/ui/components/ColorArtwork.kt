package com.getair.design.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.getair.design.model.ArtworkPalette

@Composable
fun ColorArtwork(
    palette: ArtworkPalette,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                colors = listOf(palette.start, palette.middle, palette.end),
                start = Offset.Zero,
                end = Offset.Infinite,
            )
        )
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val path = Path().apply {
                moveTo(size.width * 0.34f, 0f)
                lineTo(size.width, size.height * 0.66f)
                lineTo(size.width, size.height)
                lineTo(size.width * 0.72f, size.height)
                close()
            }
            drawPath(path, Color.White.copy(alpha = 0.055f))
            drawCircle(
                color = Color.Black.copy(alpha = 0.10f),
                radius = size.minDimension * 0.32f,
                center = Offset(size.width * 0.78f, size.height * 0.20f),
            )
        }
        content()
    }
}

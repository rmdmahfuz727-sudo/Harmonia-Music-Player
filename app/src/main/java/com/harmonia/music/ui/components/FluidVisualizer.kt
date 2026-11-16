package com.harmonia.music.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.harmonia.music.ui.theme.AccentCyan
import com.harmonia.music.ui.theme.AccentPurple
import kotlin.math.sin

/**
 * High-performance 120 FPS Canvas Audio Visualizer.
 * Bypasses Compose recomposition using graphicsLayer and direct Canvas drawing.
 */
@Composable
fun FluidVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 24,
    height: Dp = 48.dp,
    barColorStart: Color = AccentCyan,
    barColorEnd: Color = AccentPurple
) {
    val transition = rememberInfiniteTransition(label = "fluid_visualizer_phase")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .graphicsLayer {
                // Ensure RenderThread hardware layer caching for 120 FPS
                alpha = 0.95f
            }
    ) {
        val totalWidth = size.width
        val canvasHeight = size.height
        val totalSpacing = totalWidth * 0.35f
        val spacing = totalSpacing / (barCount + 1)
        val barWidth = (totalWidth - totalSpacing) / barCount

        val gradientBrush = Brush.verticalGradient(
            colors = listOf(barColorStart, barColorEnd)
        )

        for (i in 0 until barCount) {
            val normalizedX = i.toFloat() / barCount

            // Multi-frequency harmonic wave for realistic audio visualizer response
            val wave1 = sin(phase + i * 0.45f)
            val wave2 = sin(phase * 1.6f + i * 0.8f)
            val wave3 = sin(phase * 0.8f - i * 0.3f)
            val combined = (wave1 * 0.45f + wave2 * 0.35f + wave3 * 0.2f).coerceIn(-1f, 1f)

            val targetHeightFraction = if (isPlaying) {
                0.20f + (combined + 1f) * 0.38f
            } else {
                0.08f // resting minimum height when paused
            }

            val barHeight = (canvasHeight * targetHeightFraction).coerceIn(4f, canvasHeight)
            val left = spacing + i * (barWidth + spacing)
            val top = canvasHeight - barHeight

            drawRoundRect(
                brush = gradientBrush,
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}

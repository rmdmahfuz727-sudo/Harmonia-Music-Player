package com.harmonia.music.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.harmonia.music.ui.theme.MochyCoral
import com.harmonia.music.ui.theme.MochyCoralLight
import kotlin.math.sin

/**
 * Mochy-Music signature glowing wavy seek-bar slider.
 * Renders an animated harmonic wave along the active playback progress
 * with Sakuga spring response on drag/tap.
 */
@Composable
fun MochyWaveSeekBar(
    progress: Float,
    isPlaying: Boolean,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = MochyCoral,
    inactiveColor: Color = Color.White.copy(alpha = 0.22f),
    thumbColor: Color = Color.White
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_phase")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableFloatStateOf(0f) }

    // Sakuga anime spring for thumb scale on touch
    val thumbScale by animateFloatAsState(
        targetValue = if (isDragging) 1.35f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "thumb_scale"
    )

    val currentFraction = if (isDragging) dragProgress else progress.coerceIn(0f, 1f)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val newProgress = (offset.x / size.width).coerceIn(0f, 1f)
                    onSeek(newProgress)
                }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        dragProgress = (offset.x / size.width).coerceIn(0f, 1f)
                    },
                    onDragEnd = {
                        onSeek(dragProgress)
                        isDragging = false
                    },
                    onDragCancel = {
                        isDragging = false
                    },
                    onHorizontalDrag = { change, _ ->
                        dragProgress = (change.position.x / size.width).coerceIn(0f, 1f)
                    }
                )
            }
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2f
        val progressX = width * currentFraction

        // 1. Inactive Track Line
        drawLine(
            color = inactiveColor,
            start = Offset(progressX, centerY),
            end = Offset(width, centerY),
            strokeWidth = 3.5.dp.toPx(),
            cap = StrokeCap.Round
        )

        // 2. Active Track as Mochy Harmonic Wave Path
        if (progressX > 4f) {
            val wavePath = Path()
            wavePath.moveTo(0f, centerY)

            val wavePoints = (progressX / 4f).toInt().coerceAtLeast(10)
            val amplitude = if (isPlaying || isDragging) 4.5.dp.toPx() else 1.5.dp.toPx()
            val frequency = 0.055f

            for (i in 0..wavePoints) {
                val x = (i.toFloat() / wavePoints) * progressX
                val edgeFade = sin((x / progressX) * Math.PI.toFloat())
                val y = centerY + sin(x * frequency - phase) * amplitude * edgeFade
                wavePath.lineTo(x, y)
            }

            // Outer Glowing Trail
            val glowBrush = Brush.horizontalGradient(
                colors = listOf(
                    activeColor.copy(alpha = 0.35f),
                    MochyCoralLight.copy(alpha = 0.65f),
                    activeColor
                ),
                startX = 0f,
                endX = progressX
            )

            drawPath(
                path = wavePath,
                brush = glowBrush,
                style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
            )

            // Sharp Core Wave Line
            drawPath(
                path = wavePath,
                color = activeColor,
                style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // 3. Mochy Glowing Thumb with Sakuga Spring Scale
        val baseThumbRadius = 6.5.dp.toPx()
        val thumbRadius = baseThumbRadius * thumbScale
        val thumbGlowRadius = thumbRadius + 5.dp.toPx()

        // Ambient halo
        drawCircle(
            color = activeColor.copy(alpha = 0.35f),
            radius = thumbGlowRadius,
            center = Offset(progressX, centerY)
        )

        // Outer coral rim
        drawCircle(
            color = activeColor,
            radius = thumbRadius,
            center = Offset(progressX, centerY)
        )

        // Inner white nucleus
        drawCircle(
            color = thumbColor,
            radius = thumbRadius * 0.65f,
            center = Offset(progressX, centerY)
        )
    }
}

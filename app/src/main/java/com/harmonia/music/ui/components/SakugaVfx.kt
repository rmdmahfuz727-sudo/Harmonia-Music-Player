package com.harmonia.music.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.harmonia.music.ui.theme.MochyCoral
import com.harmonia.music.ui.theme.MochyCoralLight
import com.harmonia.music.ui.theme.SakugaGold
import com.harmonia.music.ui.theme.SakugaInk
import com.harmonia.music.ui.theme.SakugaPaper
import kotlin.math.cos
import kotlin.math.sin

/**
 * Sakuga VFX (https://sakuga-vfx.vercel.app/gallery/) Engine for Android.
 * Features:
 *  - Anime keyframed physics: volume-preserving squash and spring overshoot.
 *  - Canvas-drawn anime effects: starbursts, thinning shockwaves, crescent slashes,
 *    kira sparkles, and manga sound-effect typography (ドン, カチッ, シュッ, ポン, キラッ).
 *  - Strict Sakuga Rule compliance: Two tones + outline, leaving by shrinking/thinning.
 */

sealed class SakugaEffect(
    val startTime: Long,
    val durationMs: Int
) {
    fun progress(now: Long): Float {
        val elapsed = (now - startTime).coerceAtLeast(0)
        return (elapsed.toFloat() / durationMs).coerceIn(0f, 1f)
    }

    class Burst(
        val center: Offset,
        val color: Color = MochyCoral,
        val shardCount: Int = 8,
        val maxRadius: Float = 140f,
        startTime: Long = System.currentTimeMillis()
    ) : SakugaEffect(startTime, durationMs = 450)

    class Ring(
        val center: Offset,
        val color: Color = MochyCoralLight,
        val maxRadius: Float = 120f,
        startTime: Long = System.currentTimeMillis()
    ) : SakugaEffect(startTime, durationMs = 400)

    class Slash(
        val center: Offset,
        val angle: Float = -25f,
        val length: Float = 180f,
        val color: Color = SakugaPaper,
        startTime: Long = System.currentTimeMillis()
    ) : SakugaEffect(startTime, durationMs = 350)

    class Kira(
        val center: Offset,
        val size: Float = 38f,
        val color: Color = SakugaGold,
        startTime: Long = System.currentTimeMillis()
    ) : SakugaEffect(startTime, durationMs = 480)

    class MangaSfx(
        val text: String,
        val center: Offset,
        val color: Color = SakugaPaper,
        val outlineColor: Color = SakugaInk,
        startTime: Long = System.currentTimeMillis()
    ) : SakugaEffect(startTime, durationMs = 600)
}

@Stable
class SakugaVfxState {
    val activeEffects = mutableStateListOf<SakugaEffect>()

    fun triggerBurst(center: Offset, color: Color = MochyCoral, shardCount: Int = 8) {
        activeEffects.add(SakugaEffect.Burst(center, color, shardCount))
    }

    fun triggerRing(center: Offset, color: Color = MochyCoralLight, maxRadius: Float = 130f) {
        activeEffects.add(SakugaEffect.Ring(center, color, maxRadius))
    }

    fun triggerSlash(center: Offset, color: Color = SakugaPaper, angle: Float = -30f) {
        activeEffects.add(SakugaEffect.Slash(center, angle, color = color))
    }

    fun triggerKira(center: Offset, color: Color = SakugaGold, size: Float = 42f) {
        activeEffects.add(SakugaEffect.Kira(center, size, color))
    }

    fun triggerMangaSfx(text: String, center: Offset, color: Color = SakugaPaper) {
        activeEffects.add(SakugaEffect.MangaSfx(text, center, color))
    }

    fun prune(now: Long) {
        if (activeEffects.isNotEmpty()) {
            activeEffects.removeAll { (now - it.startTime) > it.durationMs }
        }
    }
}

val LocalSakugaVfx = staticCompositionLocalOf { SakugaVfxState() }

/**
 * Sakuga Volume-Preserving Squash and Spring Overshoot Modifier.
 * When pressed: squashes along Y axis while widening proportionally along X axis.
 * When released: snaps back with anime spring overshoot.
 */
fun Modifier.sakugaSquash(
    squashAmount: Float = 0.88f,
    interactionSource: MutableInteractionSource? = null,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()

    // Medium bouncy spring with snappy overshoot
    val squashY by animateFloatAsState(
        targetValue = if (isPressed) squashAmount else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "sakuga_squash_y"
    )

    // Sakuga anime rule: Squash keeps volume (scaleX * scaleY ~= 1)
    val squashX = 1f + (1f - squashY) * 0.75f

    this
        .graphicsLayer {
            scaleX = squashX
            scaleY = squashY
        }
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = source,
                    indication = null,
                    onClick = onClick
                )
            } else {
                Modifier
            }
        )
}

/**
 * High-performance 120 FPS Sakuga VFX Rendering Overlay Canvas.
 */
@Composable
fun SakugaVfxOverlay(
    vfxState: SakugaVfxState,
    modifier: Modifier = Modifier
) {
    var frameTick by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { time ->
                frameTick = time
                vfxState.prune(System.currentTimeMillis())
            }
        }
    }

    if (vfxState.activeEffects.isEmpty()) return

    val paintFill = remember {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
        }
    }

    val paintStroke = remember {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val now = System.currentTimeMillis()

        vfxState.activeEffects.forEach { effect ->
            val p = effect.progress(now)
            if (p >= 1f) return@forEach

            when (effect) {
                is SakugaEffect.Burst -> {
                    // Anime Starburst: Sharp radial wedges expanding & thinning out
                    val currentRadius = effect.maxRadius * (0.2f + 0.8f * p)
                    val shardWidth = 14f * (1f - p) // Leaves by shrinking

                    for (i in 0 until effect.shardCount) {
                        val angle = (i.toFloat() / effect.shardCount) * 2f * Math.PI.toFloat()
                        val tipX = effect.center.x + cos(angle) * currentRadius
                        val tipY = effect.center.y + sin(angle) * currentRadius

                        val baseDist = currentRadius * 0.45f
                        val perpAngle = angle + (Math.PI / 2f).toFloat()
                        val b1X = effect.center.x + cos(angle) * baseDist + cos(perpAngle) * shardWidth
                        val b1Y = effect.center.y + sin(angle) * baseDist + sin(perpAngle) * shardWidth
                        val b2X = effect.center.x + cos(angle) * baseDist - cos(perpAngle) * shardWidth
                        val b2Y = effect.center.y + sin(angle) * baseDist - sin(perpAngle) * shardWidth

                        val path = Path().apply {
                            moveTo(tipX, tipY)
                            lineTo(b1X, b1Y)
                            lineTo(b2X, b2Y)
                            close()
                        }

                        // Outline (Two tones + line)
                        drawPath(path, color = SakugaInk, style = Stroke(width = 3.5f))
                        // Vibrant Core
                        drawPath(path, color = effect.color)
                    }
                }

                is SakugaEffect.Ring -> {
                    // Shockwave ring that expands and thins out
                    val radius = effect.maxRadius * p
                    val strokeWidth = (9f * (1f - p)).coerceAtLeast(0.8f)

                    drawCircle(
                        color = effect.color,
                        radius = radius,
                        center = effect.center,
                        style = Stroke(width = strokeWidth)
                    )
                }

                is SakugaEffect.Slash -> {
                    // Crescent sword slash with tapered ends
                    val slashLength = effect.length * (0.3f + 0.7f * p)
                    val slashThickness = (12f * (1f - p)).coerceAtLeast(1f)
                    val rad = Math.toRadians(effect.angle.toDouble()).toFloat()

                    val halfL = slashLength / 2f
                    val startX = effect.center.x - cos(rad) * halfL
                    val startY = effect.center.y - sin(rad) * halfL
                    val endX = effect.center.x + cos(rad) * halfL
                    val endY = effect.center.y + sin(rad) * halfL

                    val perpRad = rad + (Math.PI / 2f).toFloat()
                    val midOffset = slashThickness * sin(p * Math.PI.toFloat())

                    val path = Path().apply {
                        moveTo(startX, startY)
                        quadraticBezierTo(
                            effect.center.x + cos(perpRad) * midOffset,
                            effect.center.y + sin(perpRad) * midOffset,
                            endX,
                            endY
                        )
                        quadraticBezierTo(
                            effect.center.x - cos(perpRad) * midOffset,
                            effect.center.y - sin(perpRad) * midOffset,
                            startX,
                            startY
                        )
                        close()
                    }

                    drawPath(path, color = SakugaInk, style = Stroke(width = 3.5f))
                    drawPath(path, color = effect.color)
                }

                is SakugaEffect.Kira -> {
                    // 4-Point Anime Sparkle Glint (expands, rotates, shrinks to zero)
                    val scale = if (p < 0.35f) {
                        (p / 0.35f) * 1.25f // Anticipation & overshoot
                    } else {
                        1.25f - ((p - 0.35f) / 0.65f) * 1.25f // Shrinking exit
                    }

                    val size = effect.size * scale
                    val rotation = (p * 45f)
                    val rad = Math.toRadians(rotation.toDouble()).toFloat()

                    val path = Path()
                    for (i in 0 until 4) {
                        val armAngle = rad + (i * Math.PI / 2f).toFloat()
                        val innerAngle = armAngle + (Math.PI / 4f).toFloat()

                        val outerX = effect.center.x + cos(armAngle) * size
                        val outerY = effect.center.y + sin(armAngle) * size
                        val innerX = effect.center.x + cos(innerAngle) * (size * 0.22f)
                        val innerY = effect.center.y + sin(innerAngle) * (size * 0.22f)

                        if (i == 0) path.moveTo(outerX, outerY) else path.lineTo(outerX, outerY)
                        path.lineTo(innerX, innerY)
                    }
                    path.close()

                    drawPath(path, color = SakugaInk, style = Stroke(width = 3.5f))
                    drawPath(path, color = effect.color)
                }

                is SakugaEffect.MangaSfx -> {
                    // Manga Sound-Effect Lettering (ドン, カチッ, etc.)
                    // Anime pop: scales up rapidly to 1.35x, drifts upward, and shrinks away
                    val scale = if (p < 0.25f) {
                        (p / 0.25f) * 1.35f
                    } else {
                        1.35f - ((p - 0.25f) / 0.75f) * 1.35f
                    }

                    val offsetY = -36f * p
                    val posX = effect.center.x
                    val posY = effect.center.y + offsetY

                    val fontSize = 38f * scale

                    drawContext.canvas.nativeCanvas.apply {
                        save()
                        rotate(-6f, posX, posY)

                        // Outline
                        paintStroke.color = effect.outlineColor.toArgb()
                        paintStroke.strokeWidth = 9f * scale
                        paintStroke.textSize = fontSize
                        paintStroke.textAlign = Paint.Align.CENTER
                        drawText(effect.text, posX, posY, paintStroke)

                        // Paper / Color Fill
                        paintFill.color = effect.color.toArgb()
                        paintFill.textSize = fontSize
                        paintFill.textAlign = Paint.Align.CENTER
                        drawText(effect.text, posX, posY, paintFill)

                        restore()
                    }
                }
            }
        }
    }
}

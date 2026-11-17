package com.harmonia.music.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.harmonia.music.R
import com.harmonia.music.model.Track
import com.harmonia.music.ui.theme.DarkSurfaceElevated
import com.harmonia.music.ui.theme.DarkSurfaceVariant
import com.harmonia.music.ui.theme.MochyCoral
import com.harmonia.music.ui.theme.MochyCoralLight
import com.harmonia.music.ui.theme.MochyPillBackground
import com.harmonia.music.ui.theme.TextPrimary
import com.harmonia.music.ui.theme.TextSecondary
import kotlinx.coroutines.launch

/**
 * Mochy-Music Signature Component Set.
 * Faithfully mirrors Mochy's design metrics, layouts, and physics.
 */

val MochyPillText = TextStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 10.sp,
    letterSpacing = (-0.3).sp,
    color = Color.White
)

/**
 * Mochy Apple-Style Volume Bar with touch swelling (6.5dp -> 12dp) and speaker icons.
 */
@Composable
fun MochyAppleVolume(
    volume: Float = 0.75f,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentVolume by remember(volume) { mutableFloatStateOf(volume) }
    var isDragging by remember { mutableStateOf(false) }

    // Swells from thin 6.5dp to thick 12dp when touched
    val heldScale by animateFloatAsState(
        targetValue = if (isDragging) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 500f),
        label = "volume_held"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.VolumeDown,
            contentDescription = "Mute / Low Volume",
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(30.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            currentVolume = (offset.x / size.width).coerceIn(0f, 1f)
                            onVolumeChange(currentVolume)
                        },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false },
                        onHorizontalDrag = { change, _ ->
                            currentVolume = (change.position.x / size.width).coerceIn(0f, 1f)
                            onVolumeChange(currentVolume)
                        }
                    )
                }
                .drawWithCache {
                    val trackThin = 6.5.dp.toPx()
                    val trackThick = 12.dp.toPx()
                    val thickness = trackThin + (trackThick - trackThin) * heldScale
                    val radius = CornerRadius(thickness / 2f)
                    val cy = size.height / 2f
                    val top = cy - thickness / 2f

                    onDrawBehind {
                        // Track Background
                        drawRoundRect(
                            color = Color.White.copy(alpha = 0.18f + 0.08f * heldScale),
                            topLeft = Offset(0f, top),
                            size = Size(size.width, thickness),
                            cornerRadius = radius
                        )

                        // Active Volume Filled
                        val fillWidth = size.width * currentVolume
                        if (fillWidth > 0.5f) {
                            drawRoundRect(
                                color = Color.White,
                                topLeft = Offset(0f, top),
                                size = Size(fillWidth.coerceAtLeast(thickness), thickness),
                                cornerRadius = radius
                            )
                        }
                    }
                }
        )

        Spacer(modifier = Modifier.width(10.dp))

        Icon(
            imageVector = Icons.Default.VolumeUp,
            contentDescription = "High Volume",
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(16.dp)
        )
    }
}

/**
 * Mochy Output Destination Pill (e.g., "CPH2745 • SpatialFX").
 */
@Composable
fun MochyOutputPill(
    label: String = "CPH2745 • SpatialFX",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White.copy(alpha = 0.10f),
        shape = RoundedCornerShape(50),
        modifier = modifier
            .height(24.dp)
            .widthIn(min = 96.dp, max = 220.dp)
            .sakugaSquash(squashAmount = 0.92f) { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 11.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Bluetooth,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = label,
                style = MochyPillText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Mochy Pages Pill (Lyrics & Queue switcher).
 */
@Composable
fun MochyPagesPill(
    selectedPage: Int, // 0 = Player, 1 = Lyrics, 2 = Queue
    onSelectPage: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White.copy(alpha = 0.10f),
        shape = RoundedCornerShape(50),
        modifier = modifier.height(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            // Lyrics Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (selectedPage == 1) Color.White.copy(alpha = 0.25f) else Color.Transparent)
                    .clickable { onSelectPage(if (selectedPage == 1) 0 else 1) }
                    .padding(horizontal = 10.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "Lyrics",
                        tint = if (selectedPage == 1) MochyCoralLight else Color.White,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Lyrics",
                        style = MochyPillText,
                        color = if (selectedPage == 1) MochyCoralLight else Color.White
                    )
                }
            }

            Spacer(Modifier.width(2.dp))

            // Queue Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (selectedPage == 2) Color.White.copy(alpha = 0.25f) else Color.Transparent)
                    .clickable { onSelectPage(if (selectedPage == 2) 0 else 2) }
                    .padding(horizontal = 10.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = "Queue",
                        tint = if (selectedPage == 2) MochyCoralLight else Color.White,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Queue",
                        style = MochyPillText,
                        color = if (selectedPage == 2) MochyCoralLight else Color.White
                    )
                }
            }
        }
    }
}

/**
 * Mochy Hop-In Quick Card for the Home Screen.
 */
@Composable
fun MochyHopInCard(
    track: Track,
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Surface(
        color = DarkSurfaceVariant.copy(alpha = 0.55f),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .sakugaSquash(squashAmount = 0.95f) { onClick() }
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Squircle Artwork
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp))
                    .background(DarkSurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(track.artworkUri)
                        .crossfade(true)
                        .error(R.drawable.default_artwork)
                        .placeholder(R.drawable.default_artwork)
                        .build(),
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                if (isPlaying) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(MochyCoral.copy(alpha = 0.35f)),
                        contentAlignment = Alignment.Center
                    ) {
                        FluidVisualizer(
                            isPlaying = true,
                            barCount = 3,
                            height = 18.dp,
                            modifier = Modifier.width(22.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.width(10.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isPlaying) MochyCoral else TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = track.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Quick Play Button
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MochyCoral.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = MochyCoral,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

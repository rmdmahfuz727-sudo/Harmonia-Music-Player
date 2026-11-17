package com.harmonia.music.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.harmonia.music.R
import com.harmonia.music.model.PlaybackState
import com.harmonia.music.ui.theme.MochyBackdropBottom
import com.harmonia.music.ui.theme.MochyBackdropGlow
import com.harmonia.music.ui.theme.MochyBackdropTop
import com.harmonia.music.ui.theme.MochyCoral
import com.harmonia.music.ui.theme.MochyCoralGlow
import com.harmonia.music.ui.theme.MochyCoralLight
import com.harmonia.music.ui.theme.MochyPillBackground
import com.harmonia.music.ui.theme.MochySubtleText
import com.harmonia.music.ui.theme.SakugaGold
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullPlayerSheet(
    playbackState: PlaybackState,
    onDismiss: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onSeek: (Long) -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val track = playbackState.currentTrack ?: return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val vfxState = remember { SakugaVfxState() }

    // Mochy interactive states
    var isDisliked by remember { mutableStateOf(false) }
    var isHapticsEnabled by remember { mutableStateOf(true) }
    var isSpatialActive by remember { mutableStateOf(true) }
    var selectedBottomPage by remember { mutableIntStateOf(0) } // 0 = standard, 1 = lyrics, 2 = queue
    var showMenu by remember { mutableStateOf(false) }
    var systemVolume by remember { mutableFloatStateOf(0.72f) }

    // Fluid spring physics for artwork breathing
    val artworkScale by animateFloatAsState(
        targetValue = if (playbackState.isPlaying) 1f else 0.94f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "artwork_scale"
    )

    // Ambient background gradient inspired by Mochy-Music cover lighting
    val backdropBrush = Brush.verticalGradient(
        colors = listOf(
            MochyBackdropTop,
            MochyBackdropGlow,
            MochyBackdropBottom
        )
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MochyBackdropBottom,
        dragHandle = null
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backdropBrush)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Top Header: Down Arrow | NOW PLAYING | Overflow Menu
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.sakugaSquash(squashAmount = 0.85f) { onDismiss() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Collapse",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Text(
                        text = "NOW PLAYING",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White.copy(alpha = 0.9f),
                        letterSpacing = 2.sp
                    )

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Color(0xFF242229))
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (track.isFavorite) "Remove from Favorites" else "Add to Favorites", color = Color.White) },
                                onClick = {
                                    onToggleFavorite()
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Shuffle: ${if (playbackState.shuffleEnabled) "On" else "Off"}", color = Color.White) },
                                onClick = {
                                    onToggleShuffle()
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Repeat: ${playbackState.repeatMode.name}", color = Color.White) },
                                onClick = {
                                    onCycleRepeat()
                                    showMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 2. Mochy Squircle Artwork Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .aspectRatio(1f)
                        .graphicsLayer {
                            scaleX = artworkScale
                            scaleY = artworkScale
                        }
                        .shadow(
                            elevation = 32.dp,
                            shape = RoundedCornerShape(32.dp),
                            spotColor = MochyCoralGlow,
                            ambientColor = Color.Black
                        )
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFF1E1C22)),
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

                    // Lyrics Overlay Page
                    if (selectedBottomPage == 1) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Color.Black.copy(alpha = 0.82f))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "♪ ♫ ♩",
                                    color = MochyCoral,
                                    fontSize = 24.sp
                                )
                                Text(
                                    text = track.title,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Lyrics synchronized via Mochy Media Engine\nEnjoy the high-fidelity soundstage",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3. Track Title & Artist (Left Aligned as in Mochy)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = track.artist.ifBlank { "Unknown Artist" },
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.72f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Mochy Action Pills Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Like / Dislike Combined Pill
                    Surface(
                        color = MochyPillBackground,
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .sakugaSquash(squashAmount = 0.85f) {
                                        onToggleFavorite()
                                        if (isDisliked) isDisliked = false
                                        vfxState.triggerKira(Offset(120f, 400f), color = SakugaGold)
                                        vfxState.triggerMangaSfx("ポン!", Offset(120f, 380f), color = MochyCoralLight)
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (track.isFavorite) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                    contentDescription = "Like",
                                    tint = if (track.isFavorite) MochyCoral else Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (track.isFavorite) "Liked" else "100K",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(14.dp)
                                    .background(Color.White.copy(alpha = 0.25f))
                            )
                            Spacer(modifier = Modifier.width(10.dp))

                            Box(
                                modifier = Modifier.sakugaSquash(squashAmount = 0.85f) {
                                    isDisliked = !isDisliked
                                    if (track.isFavorite && isDisliked) onToggleFavorite()
                                }
                            ) {
                                Icon(
                                    imageVector = if (isDisliked) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                                    contentDescription = "Dislike",
                                    tint = if (isDisliked) MochyCoral else Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Music Haptics Pill
                    Surface(
                        color = if (isHapticsEnabled) MochyPillBackground else MochyPillBackground.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier.sakugaSquash(squashAmount = 0.88f) {
                            isHapticsEnabled = !isHapticsEnabled
                            vfxState.triggerMangaSfx("カチッ!", Offset(240f, 400f), color = MochyCoral)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Vibration,
                                contentDescription = "Music Haptics",
                                tint = if (isHapticsEnabled) MochyCoral else Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Music Haptics",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Spatial Audio Pill
                    Surface(
                        color = if (isSpatialActive) MochyPillBackground else MochyPillBackground.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier.sakugaSquash(squashAmount = 0.88f) {
                            isSpatialActive = !isSpatialActive
                            vfxState.triggerRing(Offset(380f, 400f), color = MochyCoral)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.SurroundSound,
                                contentDescription = "Spatial Audio",
                                tint = if (isSpatialActive) MochyCoral else Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SpatialFX",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 5. Mochy Signature Wavy Glowing Seek Bar
                if (!track.isStream) {
                    MochyWaveSeekBar(
                        progress = playbackState.progressFraction,
                        isPlaying = playbackState.isPlaying,
                        onSeek = { fraction ->
                            val targetMs = (fraction * playbackState.durationMs).toLong()
                            onSeek(targetMs)
                        },
                        activeColor = MochyCoral,
                        inactiveColor = Color.White.copy(alpha = 0.18f),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Timestamps & Codec Pill
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(playbackState.currentPositionMs),
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Surface(
                            color = MochyPillBackground,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "🎧 • AAC",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }

                        val remainingMs = (playbackState.durationMs - playbackState.currentPositionMs).coerceAtLeast(0L)
                        Text(
                            text = "-${formatTime(remainingMs)}",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Surface(
                        color = MochyPillBackground,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "🔴 LIVE BROADCAST • ${track.streamGenre ?: "AAC High-Definition"}",
                                color = MochyCoral,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 6. Mochy Flagship Transport Cluster (Previous Capsule | Center Big Coral Pill | Next Capsule)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Previous Capsule
                    Surface(
                        color = MochyPillBackground,
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .size(width = 82.dp, height = 58.dp)
                            .sakugaSquash(squashAmount = 0.85f) {
                                if (!track.isStream) {
                                    vfxState.triggerSlash(Offset(140f, 620f), color = MochyCoralLight)
                                    vfxState.triggerMangaSfx("シュッ!", Offset(140f, 600f), color = Color.White)
                                    onSkipPrevious()
                                }
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Previous",
                                tint = if (!track.isStream) Color.White else Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(18.dp))

                    // Center Iconic Mochy Coral Pill Play/Pause FAB
                    Surface(
                        color = MochyCoral,
                        shape = RoundedCornerShape(32.dp),
                        shadowElevation = 14.dp,
                        modifier = Modifier
                            .size(width = 112.dp, height = 64.dp)
                            .sakugaSquash(squashAmount = 0.82f) {
                                vfxState.triggerBurst(Offset(270f, 620f), color = MochyCoralLight, shardCount = 8)
                                vfxState.triggerRing(Offset(270f, 620f), color = MochyCoral, maxRadius = 140f)
                                vfxState.triggerMangaSfx("ドン!", Offset(270f, 580f), color = Color.White)
                                onPlayPauseClick()
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (playbackState.isBuffering) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(30.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(18.dp))

                    // Next Capsule
                    Surface(
                        color = MochyPillBackground,
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .size(width = 82.dp, height = 58.dp)
                            .sakugaSquash(squashAmount = 0.85f) {
                                if (!track.isStream) {
                                    vfxState.triggerSlash(Offset(400f, 620f), color = MochyCoralLight)
                                    vfxState.triggerMangaSfx("シュッ!", Offset(400f, 600f), color = Color.White)
                                    onSkipNext()
                                }
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next",
                                tint = if (!track.isStream) Color.White else Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 7. Mochy Apple-Style Volume Bar with touch swelling
                MochyAppleVolume(
                    volume = systemVolume,
                    onVolumeChange = { systemVolume = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 8. Mochy Bottom Bar: OutputPill & PagesPill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MochyOutputPill(
                        label = if (isSpatialActive) "CPH2745 • SpatialFX" else "Phone Speaker",
                        onClick = { isSpatialActive = !isSpatialActive }
                    )

                    MochyPagesPill(
                        selectedPage = selectedBottomPage,
                        onSelectPage = { selectedBottomPage = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Sakuga VFX Anime Overlay Rendering Layer
            SakugaVfxOverlay(vfxState = vfxState)
        }
    }
}

private fun formatTime(millis: Long): String {
    if (millis <= 0) return "0:00"
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}

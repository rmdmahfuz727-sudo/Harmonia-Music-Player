package com.harmonia.music.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode as AnimRepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.harmonia.music.model.PlayerStyle
import com.harmonia.music.model.RepeatMode
import com.harmonia.music.model.Track
import com.harmonia.music.ui.theme.DarkSurfaceElevated
import com.harmonia.music.ui.theme.DarkSurfaceVariant
import com.harmonia.music.ui.theme.MochyBackdropBottom
import com.harmonia.music.ui.theme.MochyBackdropGlow
import com.harmonia.music.ui.theme.MochyBackdropTop
import com.harmonia.music.ui.theme.MochyCoral
import com.harmonia.music.ui.theme.MochyCoralGlow
import com.harmonia.music.ui.theme.MochyCoralLight
import com.harmonia.music.ui.theme.MochyPillBackground
import com.harmonia.music.ui.theme.SakugaGold
import com.harmonia.music.ui.theme.TextPrimary
import com.harmonia.music.ui.theme.TextSecondary
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
    onToggleFavorite: () -> Unit,
    onSelectStyle: (PlayerStyle) -> Unit = {},
    onSetPlaybackSpeed: (Float) -> Unit = {},
    onSetSleepTimer: (Int) -> Unit = {},
    onSelectQueueTrack: (Int) -> Unit = {}
) {
    val track = playbackState.currentTrack ?: return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val vfxState = remember { SakugaVfxState() }

    // Interactive states
    var isDisliked by remember { mutableStateOf(false) }
    var isHapticsEnabled by remember { mutableStateOf(true) }
    var isSpatialActive by remember { mutableStateOf(true) }
    var selectedBottomPage by remember { mutableIntStateOf(0) } // 0 = standard, 1 = lyrics, 2 = queue
    var showMenu by remember { mutableStateOf(false) }
    var showStyleDialog by remember { mutableStateOf(false) }
    var showSpeedDialog by remember { mutableStateOf(false) }
    var showTimerDialog by remember { mutableStateOf(false) }
    var systemVolume by remember { mutableFloatStateOf(0.72f) }

    // Vinyl Disc Rotation Animation
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_transition")
    val vinylRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6500, easing = LinearEasing),
            repeatMode = AnimRepeatMode.Restart
        ),
        label = "vinyl_rotation"
    )

    // Fluid spring physics for artwork breathing in Flagship style
    val artworkScale by animateFloatAsState(
        targetValue = if (playbackState.isPlaying) 1f else 0.94f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "artwork_scale"
    )

    // Ambient background gradient
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
                    .padding(horizontal = 22.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Top Header: Down Arrow | NOW PLAYING / Style Badge | Overflow Menu
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

                    // Style Switcher Pill in Top Bar
                    Surface(
                        color = MochyPillBackground,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .sakugaSquash(squashAmount = 0.9f) { showStyleDialog = true }
                            .clickable { showStyleDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Style",
                                tint = MochyCoral,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = playbackState.playerStyle.displayName.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 1.2.sp
                            )
                        }
                    }

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
                                text = { Text("Player Style: ${playbackState.playerStyle.displayName}", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.Palette, null, tint = MochyCoral) },
                                onClick = {
                                    showMenu = false
                                    showStyleDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Playback Speed: ${playbackState.playbackSpeed}x", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.Speed, null, tint = MochyCoral) },
                                onClick = {
                                    showMenu = false
                                    showSpeedDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (playbackState.sleepTimerMinutes > 0)
                                            "Sleep Timer: ${playbackState.sleepTimerMinutes}m"
                                        else "Sleep Timer: Off",
                                        color = Color.White
                                    )
                                },
                                leadingIcon = { Icon(Icons.Default.Timer, null, tint = MochyCoral) },
                                onClick = {
                                    showMenu = false
                                    showTimerDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (track.isFavorite) "Remove from Favorites" else "Add to Favorites", color = Color.White) },
                                leadingIcon = { Icon(if (track.isFavorite) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp, null, tint = MochyCoral) },
                                onClick = {
                                    onToggleFavorite()
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Shuffle: ${if (playbackState.shuffleEnabled) "On" else "Off"}", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.Shuffle, null, tint = if (playbackState.shuffleEnabled) MochyCoral else Color.White) },
                                onClick = {
                                    onToggleShuffle()
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Repeat: ${playbackState.repeatMode.name}", color = Color.White) },
                                leadingIcon = {
                                    Icon(
                                        if (playbackState.repeatMode == RepeatMode.ONE) Icons.Default.RepeatOne else Icons.Default.Repeat,
                                        null,
                                        tint = if (playbackState.repeatMode != RepeatMode.OFF) MochyCoral else Color.White
                                    )
                                },
                                onClick = {
                                    onCycleRepeat()
                                    showMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 2. Main Middle Viewport (Scrollable or Page Controlled)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        // Page 1: Synchronized Real-Time Lyrics View
                        selectedBottomPage == 1 || playbackState.playerStyle == PlayerStyle.LYRICS -> {
                            SynchronizedLyricsView(
                                playbackState = playbackState,
                                track = track,
                                onSeek = onSeek,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Page 2: Playback Queue View
                        selectedBottomPage == 2 -> {
                            QueueListView(
                                playbackState = playbackState,
                                onSelectTrack = { index -> onSelectQueueTrack(index) },
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Page 0: Dynamic Player Styles
                        else -> {
                            when (playbackState.playerStyle) {
                                PlayerStyle.FLAGSHIP -> {
                                    // Mochy Flagship Squircle Card
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
                                    }
                                }

                                PlayerStyle.VINYL -> {
                                    // Vinyl Turntable Player
                                    VinylTurntableView(
                                        track = track,
                                        isPlaying = playbackState.isPlaying,
                                        rotationAngle = if (playbackState.isPlaying) vinylRotation else 0f
                                    )
                                }

                                PlayerStyle.CANVAS -> {
                                    // Minimal Canvas Mode (Full-bleed cover with ambient blur)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(24.dp))
                                            .background(Color(0xFF15151B)),
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
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.verticalGradient(
                                                        listOf(
                                                            Color.Black.copy(alpha = 0.3f),
                                                            Color.Transparent,
                                                            Color.Black.copy(alpha = 0.85f)
                                                        )
                                                    )
                                                )
                                        )
                                    }
                                }

                                PlayerStyle.LYRICS -> {
                                    // Handled in condition above
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

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

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = track.artist.ifBlank { "Unknown Artist" },
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.72f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

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
                                    text = if (track.isFavorite) "Liked" else "Favorite",
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

                Spacer(modifier = Modifier.height(16.dp))

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

                    // Timestamps & Audio Format Pill
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
                                text = if (track.isYouTube) "▶ • YT AUDIO 320K" else "🎧 • AAC HD",
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
                }

                Spacer(modifier = Modifier.height(18.dp))

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
                            .size(width = 82.dp, height = 56.dp)
                            .sakugaSquash(squashAmount = 0.85f) {
                                vfxState.triggerSlash(Offset(140f, 620f), color = MochyCoralLight)
                                vfxState.triggerMangaSfx("シュッ!", Offset(140f, 600f), color = Color.White)
                                onSkipPrevious()
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Previous",
                                tint = Color.White,
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
                            .size(width = 110.dp, height = 62.dp)
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
                                    modifier = Modifier.size(28.dp)
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
                            .size(width = 82.dp, height = 56.dp)
                            .sakugaSquash(squashAmount = 0.85f) {
                                vfxState.triggerSlash(Offset(400f, 620f), color = MochyCoralLight)
                                vfxState.triggerMangaSfx("シュッ!", Offset(400f, 600f), color = Color.White)
                                onSkipNext()
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 7. Mochy Apple-Style Volume Bar with touch swelling
                MochyAppleVolume(
                    volume = systemVolume,
                    onVolumeChange = { systemVolume = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 8. Mochy Bottom Bar: OutputPill & PagesPill (Lyrics / Queue)
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

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Sakuga VFX Anime Overlay Rendering Layer
            SakugaVfxOverlay(vfxState = vfxState)
        }
    }

    // Player Style Switcher Dialog
    if (showStyleDialog) {
        AlertDialog(
            onDismissRequest = { showStyleDialog = false },
            containerColor = DarkSurfaceVariant,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text("Select Player Style", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PlayerStyle.values().forEach { style ->
                        val isSelected = playbackState.playerStyle == style
                        Surface(
                            color = if (isSelected) MochyCoral.copy(alpha = 0.2f) else MochyPillBackground,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectStyle(style)
                                    showStyleDialog = false
                                }
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.dp,
                                    color = if (isSelected) MochyCoral else Color.Transparent,
                                    shape = RoundedCornerShape(14.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = style.displayName,
                                    color = if (isSelected) MochyCoral else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                                if (isSelected) {
                                    Icon(Icons.Default.Check, null, tint = MochyCoral, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                OutlinedButton(onClick = { showStyleDialog = false }) {
                    Text("Close", color = TextSecondary)
                }
            }
        )
    }

    // Playback Speed Dialog
    if (showSpeedDialog) {
        val speeds = listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
        AlertDialog(
            onDismissRequest = { showSpeedDialog = false },
            containerColor = DarkSurfaceVariant,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text("Playback Speed", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    speeds.forEach { speed ->
                        val isSelected = playbackState.playbackSpeed == speed
                        Surface(
                            color = if (isSelected) MochyCoral.copy(alpha = 0.2f) else MochyPillBackground,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSetPlaybackSpeed(speed)
                                    showSpeedDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (speed == 1.0f) "1.0x (Normal)" else "${speed}x",
                                    color = if (isSelected) MochyCoral else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                                if (isSelected) {
                                    Icon(Icons.Default.Check, null, tint = MochyCoral, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                OutlinedButton(onClick = { showSpeedDialog = false }) {
                    Text("Close", color = TextSecondary)
                }
            }
        )
    }

    // Sleep Timer Dialog
    if (showTimerDialog) {
        val timerOptions = listOf(0, 15, 30, 45, 60)
        AlertDialog(
            onDismissRequest = { showTimerDialog = false },
            containerColor = DarkSurfaceVariant,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text("Sleep Timer", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    timerOptions.forEach { minutes ->
                        val isSelected = playbackState.sleepTimerMinutes == minutes
                        Surface(
                            color = if (isSelected) MochyCoral.copy(alpha = 0.2f) else MochyPillBackground,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSetSleepTimer(minutes)
                                    showTimerDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (minutes == 0) "Turn Off Timer" else "$minutes Minutes",
                                    color = if (isSelected) MochyCoral else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                                if (isSelected) {
                                    Icon(Icons.Default.Check, null, tint = MochyCoral, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                OutlinedButton(onClick = { showTimerDialog = false }) {
                    Text("Close", color = TextSecondary)
                }
            }
        )
    }
}

/**
 * Vinyl Turntable Player:
 * Rotating dark vinyl record disc with grooved sound rings and spinning album artwork in the center label.
 */
@Composable
private fun VinylTurntableView(
    track: Track,
    isPlaying: Boolean,
    rotationAngle: Float,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth(0.92f)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        // Outer Vinyl Disc with Grooves
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotationAngle)
                .shadow(elevation = 28.dp, shape = CircleShape, spotColor = MochyCoralGlow)
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f

            // Vinyl Base
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF202026), Color(0xFF0F0F12), Color(0xFF070709)),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )

            // Fine Vinyl Grooves
            val grooveCount = 14
            for (i in 3 until grooveCount) {
                val r = radius * (i.toFloat() / grooveCount)
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = r,
                    center = center,
                    style = Stroke(width = 1.2f)
                )
            }

            // Outer Vinyl Rim Accent
            drawCircle(
                color = Color.White.copy(alpha = 0.12f),
                radius = radius - 2f,
                center = center,
                style = Stroke(width = 2.5f)
            )
        }

        // Center Album Art Circular Label
        Box(
            modifier = Modifier
                .fillMaxWidth(0.42f)
                .aspectRatio(1f)
                .rotate(rotationAngle)
                .clip(CircleShape)
                .border(3.dp, Color(0xFF1B1B20), CircleShape),
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
                modifier = Modifier.fillMaxSize()
            )

            // Center Spindle Hole
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0A0A0C))
                    .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
            )
        }
    }
}

/**
 * Real-Time Synchronized Lyrics View with smooth auto-scroll and tap-to-seek.
 */
@Composable
private fun SynchronizedLyricsView(
    playbackState: PlaybackState,
    track: Track,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val lyrics = playbackState.lyrics
    val listState = rememberLazyListState()

    // Determine currently active lyric line index based on current playback position
    val activeIndex = remember(playbackState.currentPositionMs, lyrics) {
        val synced = lyrics?.syncedLyrics ?: emptyList()
        if (synced.isEmpty()) -1
        else synced.indexOfLast { it.timeMs <= playbackState.currentPositionMs }
    }

    // Auto-scroll list when line changes
    LaunchedEffect(activeIndex) {
        if (activeIndex >= 0) {
            val targetScroll = (activeIndex - 2).coerceAtLeast(0)
            listState.animateScrollToItem(targetScroll)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.Black.copy(alpha = 0.45f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            playbackState.isLoadingLyrics -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    CircularProgressIndicator(color = MochyCoral, strokeWidth = 3.dp, modifier = Modifier.size(36.dp))
                    Text(
                        text = "Searching LRCLIB for synchronized lyrics...",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            lyrics != null && lyrics.syncedLyrics.isNotEmpty() -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(lyrics.syncedLyrics) { index, line ->
                        val isCurrent = index == activeIndex
                        Text(
                            text = line.text.ifBlank { "♪ ♪ ♪" },
                            fontSize = if (isCurrent) 22.sp else 16.sp,
                            fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isCurrent) Color.White else Color.White.copy(alpha = 0.35f),
                            lineHeight = if (isCurrent) 28.sp else 22.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSeek(line.timeMs) }
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                        )
                    }
                }
            }

            lyrics != null && !lyrics.plainLyrics.isNullOrBlank() -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 30.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(lyrics.plainLyrics.split("\n")) { _, line ->
                        Text(
                            text = line,
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.75f),
                            lineHeight = 24.sp,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                        )
                    }
                }
            }

            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = MochyCoral.copy(alpha = 0.7f),
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = "No synchronized lyrics found",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Enjoy the high-fidelity soundstage for\n${track.title}",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Playback Queue List View in the bottom sheet.
 */
@Composable
private fun QueueListView(
    playbackState: PlaybackState,
    onSelectTrack: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val queue = playbackState.queue

    if (queue.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("Queue is empty", color = Color.White.copy(alpha = 0.6f))
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "UP NEXT (${queue.size} TRACKS)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MochyCoral,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                )
            }

            itemsIndexed(queue) { index, itemTrack ->
                val isCurrent = index == playbackState.queueIndex
                Surface(
                    color = if (isCurrent) MochyCoral.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectTrack(index) }
                        .border(
                            width = if (isCurrent) 1.dp else 0.dp,
                            color = if (isCurrent) MochyCoral.copy(alpha = 0.6f) else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = if (isCurrent) MochyCoral else Color.White.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.width(26.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = itemTrack.title,
                                color = if (isCurrent) MochyCoralLight else Color.White,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = itemTrack.artist,
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (isCurrent) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "Playing",
                                tint = MochyCoral,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    if (millis <= 0) return "0:00"
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}

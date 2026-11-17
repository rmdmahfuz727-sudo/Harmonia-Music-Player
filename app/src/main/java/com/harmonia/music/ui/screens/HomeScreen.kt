package com.harmonia.music.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.harmonia.music.model.PlaybackState
import com.harmonia.music.model.Track
import com.harmonia.music.ui.components.sakugaSquash
import com.harmonia.music.ui.theme.DarkSurfaceElevated
import com.harmonia.music.ui.theme.DarkSurfaceVariant
import com.harmonia.music.ui.theme.MochyCoral
import com.harmonia.music.ui.theme.MochyCoralLight
import com.harmonia.music.ui.theme.TextPrimary
import com.harmonia.music.ui.theme.TextSecondary

/**
 * Mochy Home Screen:
 * Focused 100% on YouTube Music online streaming and music discovery:
 *  - Cinematic Hero "Stream Online" banner with top hit backdrop
 *  - Quick mood category chips
 *  - Horizontal squircle carousel of curated online hits
 *  - Vertical "Trending Online Songs" list with 1-tap playback
 *  (No local library clutter on Home tab)
 */
@Composable
fun HomeScreen(
    trendingTracks: List<Track>,
    playbackState: PlaybackState,
    onYtTrackClick: (Track) -> Unit,
    onMoodSelect: (String) -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val heroTrack = playbackState.currentTrack ?: trendingTracks.firstOrNull()

    val onlineMoods = listOf(
        "Top Global", "Pop Hits", "Hip-Hop", "Lo-Fi Beats",
        "Anime OST", "R&B", "Rock Classics", "EDM Dance"
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Top Header with Mochy Logo & Avatar ─────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mochy Play/Arrow Logo
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E1E24))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Harmonia",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Profile Avatar (Mahfuz Rahman)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF282832))
                        .border(1.5.dp, MochyCoral.copy(alpha = 0.5f), CircleShape)
                        .sakugaSquash(squashAmount = 0.88f) { onOpenProfile() }
                        .clickable { onOpenProfile() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "MR",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // ── Cinematic Hero "Stream Online" Card ─────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(280.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFF16161C))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(26.dp))
            ) {
                if (heroTrack?.artworkUri != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(heroTrack.artworkUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = heroTrack.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                }

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.15f),
                                    Color.Black.copy(alpha = 0.6f),
                                    Color.Black.copy(alpha = 0.95f)
                                )
                            )
                        )
                )

                // Bottom Content
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFF0000).copy(alpha = 0.25f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "YT MUSIC ONLINE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFF5252)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = heroTrack?.let { "${it.title} - ${it.artist}" } ?: "Discover Online Music",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // "▶ Stream Online" Pill Button
                    Surface(
                        color = Color.White.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier
                            .sakugaSquash(squashAmount = 0.9f) {
                                heroTrack?.let { onYtTrackClick(it) }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (playbackState.isPlaying && playbackState.currentTrack?.id == heroTrack?.id) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (playbackState.isPlaying && playbackState.currentTrack?.id == heroTrack?.id) "Playing Online" else "Stream Online",
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // ── Quick Mood Chips Row ────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                onlineMoods.forEach { mood ->
                    Surface(
                        color = Color(0xFF1E1E24),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .sakugaSquash(squashAmount = 0.88f) { onMoodSelect(mood) }
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(18.dp))
                    ) {
                        Text(
                            text = mood,
                            color = TextPrimary,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // ── "Trending on YouTube Music" Carousel ─────────────────────────
        item {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = MochyCoral,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Trending on YouTube Music",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    trendingTracks.take(8).forEachIndexed { index, track ->
                        val cardBg = when (index % 4) {
                            0 -> Color(0xFF8B1E2E)
                            1 -> Color(0xFF1E3528)
                            2 -> Color(0xFF382342)
                            else -> Color(0xFF1C2C3D)
                        }

                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(cardBg)
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(22.dp))
                                .sakugaSquash(squashAmount = 0.93f) { onYtTrackClick(track) }
                        ) {
                            if (!track.artworkUri.isNullOrEmpty()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(track.artworkUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = track.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.matchParentSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                            )
                                        )
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = track.title,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = track.artist,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.75f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── Popular Online Songs List ────────────────────────────────────
        item {
            Text(
                text = "Popular Online Hits",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }

        if (trendingTracks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MochyCoral,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        } else {
            items(trendingTracks.drop(8), key = { it.id }) { track ->
                OnlineTrackRowItem(
                    track = track,
                    isCurrentTrack = playbackState.currentTrack?.id == track.id,
                    isPlaying = playbackState.isPlaying,
                    onClick = { onYtTrackClick(track) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun OnlineTrackRowItem(
    track: Track,
    isCurrentTrack: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .sakugaSquash(squashAmount = 0.96f) { onClick() }
            .then(
                if (isCurrentTrack) {
                    Modifier.border(1.5.dp, MochyCoral, RoundedCornerShape(16.dp))
                } else {
                    Modifier.border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                }
            ),
        color = if (isCurrentTrack) Color(0xFF251F28) else Color(0xFF16161B),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                if (!track.artworkUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(track.artworkUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = track.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = MochyCoral,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // YT Music Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFFF0000).copy(alpha = 0.15f))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "YT MUSIC",
                        color = Color(0xFFFF5252),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isCurrentTrack) MochyCoral else TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = buildString {
                        append(track.artist)
                        if (track.album.isNotBlank()) append(" • ${track.album}")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (track.durationMs > 0) {
                Text(
                    text = track.formattedDuration,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isCurrentTrack && isPlaying) MochyCoral else Color.White.copy(alpha = 0.1f))
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCurrentTrack && isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

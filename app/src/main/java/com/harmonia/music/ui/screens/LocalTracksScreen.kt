package com.harmonia.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harmonia.music.model.PlaybackState
import com.harmonia.music.model.Track
import com.harmonia.music.ui.components.TrackItem
import com.harmonia.music.ui.components.sakugaSquash
import com.harmonia.music.ui.theme.DarkSurfaceVariant
import com.harmonia.music.ui.theme.MochyCoral
import com.harmonia.music.ui.theme.MochyPillBackground
import com.harmonia.music.ui.theme.TextPrimary
import com.harmonia.music.ui.theme.TextSecondary

@Composable
fun LocalTracksScreen(
    tracks: List<Track>,
    playbackState: PlaybackState,
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    onTrackClick: (Track, List<Track>) -> Unit,
    onShuffleAll: (List<Track>) -> Unit,
    onFavoriteToggle: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!hasPermission) {
        PermissionRequestView(onRequestPermission = onRequestPermission, modifier = modifier)
        return
    }

    if (tracks.isEmpty()) {
        EmptyTracksView(modifier = modifier)
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Mochy Quick Action Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${tracks.size} songs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Mochy Shuffle Capsule Pill with Sakuga Squash
                    Surface(
                        color = MochyPillBackground,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .sakugaSquash(squashAmount = 0.88f) {
                                onShuffleAll(tracks)
                            }
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = "Shuffle",
                                tint = MochyCoral,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Shuffle",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Mochy Play All Coral Pill with Sakuga Squash
                    Surface(
                        color = MochyCoral,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .sakugaSquash(squashAmount = 0.88f) {
                                tracks.firstOrNull()?.let { onTrackClick(it, tracks) }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play All",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Play All",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Track List
        itemsIndexed(tracks, key = { _, track -> track.id }) { _, track ->
            TrackItem(
                track = track,
                isCurrentTrack = playbackState.currentTrack?.id == track.id,
                isPlaying = playbackState.isPlaying,
                onTrackClick = { onTrackClick(track, tracks) },
                onFavoriteToggle = { onFavoriteToggle(track) }
            )
        }
    }
}

@Composable
private fun PermissionRequestView(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = DarkSurfaceVariant,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MochyCoral.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = "Storage",
                        tint = MochyCoral,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Audio Storage Permission",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Harmonia needs permission to access your audio files to discover and play songs stored on your device.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onRequestPermission,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MochyCoral,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .sakugaSquash(squashAmount = 0.92f) { onRequestPermission() }
                ) {
                    Text(
                        text = "Grant Storage Access",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyTracksView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = MochyCoral.copy(alpha = 0.6f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Music Found",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Add audio files (MP3, FLAC, AAC) to your Music or Downloads folder to listen offline.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

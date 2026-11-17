package com.harmonia.music.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harmonia.music.model.PlaybackState
import com.harmonia.music.model.Track
import com.harmonia.music.ui.components.StreamItem
import com.harmonia.music.ui.components.TrackItem
import com.harmonia.music.ui.components.sakugaSquash
import com.harmonia.music.ui.theme.MochyCoral
import com.harmonia.music.ui.theme.MochyPillBackground
import com.harmonia.music.ui.theme.TextPrimary
import com.harmonia.music.ui.theme.TextSecondary

@Composable
fun FavoritesScreen(
    favorites: List<Track>,
    playbackState: PlaybackState,
    onTrackClick: (Track, List<Track>) -> Unit,
    onShuffleFavorites: (List<Track>) -> Unit,
    onFavoriteToggle: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    if (favorites.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = MochyCoral.copy(alpha = 0.5f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Favorites Yet",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Tap the heart icon on any song or live radio stream to pin it to your favorites.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${favorites.size} saved items",
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
                                onShuffleFavorites(favorites)
                            }
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = "Shuffle Favorites",
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
                                favorites.firstOrNull()?.let { onTrackClick(it, favorites) }
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

        items(favorites, key = { it.id }) { item ->
            if (item.isStream) {
                StreamItem(
                    stream = item,
                    isCurrentTrack = playbackState.currentTrack?.id == item.id,
                    isPlaying = playbackState.isPlaying,
                    onStreamClick = { onTrackClick(item, listOf(item)) },
                    onFavoriteToggle = { onFavoriteToggle(item) }
                )
            } else {
                TrackItem(
                    track = item,
                    isCurrentTrack = playbackState.currentTrack?.id == item.id,
                    isPlaying = playbackState.isPlaying,
                    onTrackClick = { onTrackClick(item, favorites) },
                    onFavoriteToggle = { onFavoriteToggle(item) }
                )
            }
        }
    }
}

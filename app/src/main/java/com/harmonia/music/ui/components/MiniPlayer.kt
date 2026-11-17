package com.harmonia.music.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.harmonia.music.R
import com.harmonia.music.model.PlaybackState
import com.harmonia.music.ui.theme.DarkSurfaceElevated
import com.harmonia.music.ui.theme.MochyCoral
import com.harmonia.music.ui.theme.MochyCoralGlow
import com.harmonia.music.ui.theme.MochyPillBackground
import com.harmonia.music.ui.theme.MochySubtleText
import com.harmonia.music.ui.theme.TextPrimary
import com.harmonia.music.ui.theme.TextSecondary

/**
 * Mochy-styled Frosted Capsule Mini Player with Sakuga Spring Squash Physics.
 */
@Composable
fun MiniPlayer(
    playbackState: PlaybackState,
    onPlayPauseClick: () -> Unit,
    onSkipNextClick: () -> Unit,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val track = playbackState.currentTrack ?: return
    val context = LocalContext.current
    val vfx = LocalSakugaVfx.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .shadow(16.dp, RoundedCornerShape(22.dp), spotColor = MochyCoralGlow)
            .sakugaSquash(squashAmount = 0.96f) {
                onExpandClick()
            }
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, MochyCoral.copy(alpha = 0.35f), RoundedCornerShape(22.dp)),
        color = MochyPillBackground,
        shape = RoundedCornerShape(22.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mochy Squircle Artwork Tile
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
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

                Spacer(modifier = Modifier.width(12.dp))

                // Track Title & Artist
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (track.isStream) "LIVE • ${track.artist}" else track.artist,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (track.isStream) MochySubtleText else TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Mini Fluid Visualizer (6 bars)
                if (playbackState.isPlaying) {
                    FluidVisualizer(
                        isPlaying = true,
                        barCount = 6,
                        height = 20.dp,
                        modifier = Modifier.width(36.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Mochy Pill Play/Pause Button with Sakuga Squash
                Surface(
                    color = MochyCoral,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .size(width = 44.dp, height = 40.dp)
                        .sakugaSquash(squashAmount = 0.85f) {
                            onPlayPauseClick()
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (playbackState.isBuffering) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Skip Next button with Sakuga Squash
                Surface(
                    color = Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .size(width = 38.dp, height = 40.dp)
                        .sakugaSquash(squashAmount = 0.85f) {
                            onSkipNextClick()
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next Track",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Bottom Mochy Coral Progress Indicator
            if (!track.isStream && playbackState.durationMs > 0) {
                LinearProgressIndicator(
                    progress = { playbackState.progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.5.dp),
                    color = MochyCoral,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }
        }
    }
}

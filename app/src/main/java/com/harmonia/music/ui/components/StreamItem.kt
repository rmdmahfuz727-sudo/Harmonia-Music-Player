package com.harmonia.music.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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

/**
 * Mochy-styled Live Stream Station Card with Sakuga Spring Physics.
 */
@Composable
fun StreamItem(
    stream: Track,
    isCurrentTrack: Boolean,
    isPlaying: Boolean,
    onStreamClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(750),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .sakugaSquash(squashAmount = 0.96f) {
                onStreamClick()
            }
            .then(
                if (isCurrentTrack) {
                    Modifier.border(1.5.dp, MochyCoral, RoundedCornerShape(18.dp))
                } else {
                    Modifier.border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(18.dp))
                }
            ),
        color = if (isCurrentTrack) MochyPillBackground else DarkSurfaceVariant.copy(alpha = 0.65f),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Station Artwork or Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (!stream.artworkUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(stream.artworkUri)
                            .crossfade(true)
                            .error(R.drawable.default_artwork)
                            .placeholder(R.drawable.default_artwork)
                            .build(),
                        contentDescription = stream.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Radio,
                        contentDescription = "Radio",
                        tint = MochyCoral,
                        modifier = Modifier.size(28.dp)
                    )
                }

                if (isCurrentTrack && isPlaying) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(DarkSurfaceElevated.copy(alpha = 0.65f)),
                        contentAlignment = Alignment.Center
                    ) {
                        FluidVisualizer(
                            isPlaying = true,
                            barCount = 4,
                            height = 24.dp,
                            modifier = Modifier.width(30.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Station Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Mochy Coral LIVE Pill Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MochyCoral.copy(alpha = 0.2f))
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .graphicsLayer {
                                        alpha = if (isCurrentTrack && isPlaying) pulseAlpha else 1f
                                    }
                                    .clip(CircleShape)
                                    .background(MochyCoral)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "LIVE",
                                color = MochyCoral,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    stream.streamGenre?.let { genre ->
                        Text(
                            text = genre,
                            style = MaterialTheme.typography.labelSmall,
                            color = MochyCoralLight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stream.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isCurrentTrack) MochyCoral else TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = stream.artist.ifBlank { "High-Definition Audio Broadcast" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Favorite Button with Sakuga Squash
            IconButton(
                onClick = onFavoriteToggle,
                modifier = Modifier
                    .size(36.dp)
                    .sakugaSquash(squashAmount = 0.8f) {
                        onFavoriteToggle()
                    }
            ) {
                Icon(
                    imageVector = if (stream.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (stream.isFavorite) "Remove favorite" else "Add favorite",
                    tint = if (stream.isFavorite) MochyCoral else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Quick Play/Pause Mochy Button with Sakuga Squash
            FilledIconButton(
                onClick = onStreamClick,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (isCurrentTrack && isPlaying) MochyCoral else MochyPillBackground,
                    contentColor = if (isCurrentTrack && isPlaying) Color.White else TextPrimary
                ),
                modifier = Modifier
                    .size(40.dp)
                    .sakugaSquash(squashAmount = 0.85f) {
                        onStreamClick()
                    }
            ) {
                Icon(
                    imageVector = if (isCurrentTrack && isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isCurrentTrack && isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

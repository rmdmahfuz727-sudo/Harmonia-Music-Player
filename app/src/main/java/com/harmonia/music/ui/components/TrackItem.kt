package com.harmonia.music.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.harmonia.music.R
import com.harmonia.music.model.Track
import com.harmonia.music.ui.theme.DarkSurfaceElevated
import com.harmonia.music.ui.theme.DarkSurfaceVariant
import com.harmonia.music.ui.theme.MochyCoral
import com.harmonia.music.ui.theme.MochyPillBackground
import com.harmonia.music.ui.theme.TextPrimary
import com.harmonia.music.ui.theme.TextSecondary

/**
 * Mochy-styled Track Item row with Sakuga volume-preserving spring squash.
 */
@Composable
fun TrackItem(
    track: Track,
    isCurrentTrack: Boolean,
    isPlaying: Boolean,
    onTrackClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val itemBackground = if (isCurrentTrack) {
        MochyPillBackground
    } else {
        DarkSurfaceVariant.copy(alpha = 0.45f)
    }

    val itemBorder = if (isCurrentTrack) {
        Modifier.border(1.dp, MochyCoral.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
    } else {
        Modifier
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(itemBorder)
            .clip(RoundedCornerShape(16.dp))
            .background(itemBackground)
            .sakugaSquash(squashAmount = 0.96f) {
                onTrackClick()
            }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mochy Squircle Album Artwork
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
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

            if (isCurrentTrack && isPlaying) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(MochyCoral.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    FluidVisualizer(
                        isPlaying = true,
                        barCount = 4,
                        height = 20.dp,
                        modifier = Modifier.width(28.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title & Artist
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isCurrentTrack) FontWeight.Bold else FontWeight.Medium,
                color = if (isCurrentTrack) MochyCoral else TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${track.artist} • ${track.album}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Duration
        Text(
            text = track.formattedDuration,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )

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
                imageVector = if (track.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (track.isFavorite) "Remove from favorites" else "Add to favorites",
                tint = if (track.isFavorite) MochyCoral else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

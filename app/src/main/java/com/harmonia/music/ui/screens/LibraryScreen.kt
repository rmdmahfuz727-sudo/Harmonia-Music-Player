package com.harmonia.music.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.harmonia.music.R
import com.harmonia.music.model.PlaybackState
import com.harmonia.music.model.Track
import com.harmonia.music.ui.components.TrackItem
import com.harmonia.music.ui.components.sakugaSquash
import com.harmonia.music.ui.theme.DarkSurfaceElevated
import com.harmonia.music.ui.theme.DarkSurfaceVariant
import com.harmonia.music.ui.theme.MochyCoral
import com.harmonia.music.ui.theme.TextPrimary
import com.harmonia.music.ui.theme.TextSecondary

/**
 * Mochy Library Screen:
 * Matches media_1794913111822_834f83c9.jpg & media_1794913111824_d6b41b4d.jpg:
 *  - "Library" header + Profile avatar
 *  - "Search your library" capsule
 *  - Filter chips: All, Local & downloaded, Cached, Imported
 *  - "Import your music here!" card with platform badges
 *  - 2x2 grid stats: Songs, Playlists, Albums, Artists
 *  - Full-width "Recently added" & "Liked" cards
 *  - Track list discovery
 */
@Composable
fun LibraryScreen(
    tracks: List<Track>,
    favorites: List<Track>,
    playbackState: PlaybackState,
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    onTrackClick: (Track, List<Track>) -> Unit,
    onFavoriteToggle: (Track) -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf("All") }
    var librarySearchQuery by remember { mutableStateOf("") }
    var showImportCard by remember { mutableStateOf(true) }

    val filterChips = listOf("All", "Local & downloaded", "Cached", "Imported")

    val filteredTracks = remember(tracks, librarySearchQuery, selectedFilter) {
        var list = tracks
        if (selectedFilter == "Cached") list = emptyList()
        if (selectedFilter == "Imported") list = emptyList()
        if (librarySearchQuery.isNotBlank()) {
            list = list.filter {
                it.title.contains(librarySearchQuery, ignoreCase = true) ||
                it.artist.contains(librarySearchQuery, ignoreCase = true) ||
                it.album.contains(librarySearchQuery, ignoreCase = true)
            }
        }
        list
    }

    val albumsCount = remember(tracks) { tracks.map { it.album }.filter { it.isNotBlank() }.distinct().size }
    val artistsCount = remember(tracks) { tracks.map { it.artist }.filter { it.isNotBlank() }.distinct().size }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ── Top Header: Title & Avatar ──────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Library",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )

                // Profile Avatar (Mahfuz Rahman)
                Box(
                    modifier = Modifier
                        .size(38.dp)
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
                        fontSize = 13.sp
                    )
                }
            }
        }

        // ── "Search your library" Capsule ───────────────────────────────
        item {
            OutlinedTextField(
                value = librarySearchQuery,
                onValueChange = { librarySearchQuery = it },
                placeholder = {
                    Text(
                        text = "Search your library",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MochyCoral,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color(0xFF1E1E24),
                    unfocusedContainerColor = Color(0xFF1E1E24),
                    cursorColor = MochyCoral,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        // ── Filter Pills Row: All, Local & downloaded, Cached, Imported ─
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterChips.forEach { chip ->
                    val isSelected = selectedFilter == chip
                    Surface(
                        color = if (isSelected) Color(0xFF32323C) else Color(0xFF1E1E24),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .sakugaSquash(squashAmount = 0.9f) { selectedFilter = chip }
                            .clickable { selectedFilter = chip }
                            .then(
                                if (isSelected) Modifier.border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                                else Modifier
                            )
                    ) {
                        Text(
                            text = chip,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // ── "Import your music here!" Card ──────────────────────────────
        if (showImportCard) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF16161C))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    // Close "x" button on top right
                    IconButton(
                        onClick = { showImportCard = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Column {
                        // Platform badges row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PlatformIconBadge(name = "YT", bg = Color(0xFFFF0000))
                            PlatformIconBadge(name = "SP", bg = Color(0xFF1DB954))
                            PlatformIconBadge(name = "AM", bg = Color(0xFFFA243C))
                            PlatformIconBadge(name = "TD", bg = Color(0xFF000000))
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Import your music here!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // ── 2x2 Grid Stats Cards: Songs, Playlists, Albums, Artists ─────
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Row 1: Songs & Playlists
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LibraryStatCard(
                        title = "Songs",
                        count = tracks.size.toString(),
                        coverUri = tracks.firstOrNull()?.artworkUri,
                        modifier = Modifier.weight(1f)
                    )
                    LibraryStatCard(
                        title = "Playlists",
                        count = "6",
                        coverUri = tracks.getOrNull(1)?.artworkUri,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2: Albums & Artists
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LibraryStatCard(
                        title = "Albums",
                        count = albumsCount.coerceAtLeast(1).toString(),
                        coverUri = tracks.getOrNull(2)?.artworkUri,
                        modifier = Modifier.weight(1f)
                    )
                    LibraryStatCard(
                        title = "Artists",
                        count = artistsCount.coerceAtLeast(1).toString(),
                        coverUri = tracks.getOrNull(3)?.artworkUri,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // ── Full-Width Cards: "Recently added" & "Liked" ────────────────
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // "Recently added" Card
                LibraryWideCard(
                    title = "Recently added",
                    count = tracks.size.toString(),
                    coverUri = tracks.firstOrNull()?.artworkUri
                )

                // "Liked" Card with Heart icon
                LibraryWideCard(
                    title = "Liked",
                    count = favorites.size.toString(),
                    coverUri = favorites.firstOrNull()?.artworkUri,
                    isLiked = true
                )
            }
        }

        // ── Storage Permission Banner if not granted ────────────────────
        if (!hasPermission) {
            item {
                Surface(
                    color = Color(0xFF1E1E24),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .border(1.dp, MochyCoral.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Grant Audio Permission",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Allow Harmonia to scan and play high-res local audio files stored on your device.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onRequestPermission,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MochyCoral,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Grant Permission", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ── Songs List ──────────────────────────────────────────────────
        if (filteredTracks.isNotEmpty()) {
            item {
                Text(
                    text = "Songs (${filteredTracks.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }

            items(filteredTracks, key = { it.id }) { track ->
                TrackItem(
                    track = track,
                    isCurrentTrack = playbackState.currentTrack?.id == track.id,
                    isPlaying = playbackState.isPlaying,
                    onTrackClick = { onTrackClick(track, filteredTracks) },
                    onFavoriteToggle = { onFavoriteToggle(track) }
                )
            }
        }
    }
}

@Composable
private fun PlatformIconBadge(name: String, bg: Color) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun LibraryStatCard(
    title: String,
    count: String,
    coverUri: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .height(86.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF16161B))
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
        }

        // Cover peek
        if (!coverUri.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .align(Alignment.CenterEnd)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurfaceElevated)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(coverUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            }
        }
    }
}

@Composable
private fun LibraryWideCard(
    title: String,
    count: String,
    coverUri: String?,
    isLiked: Boolean = false
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF16161B))
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
        }

        // Right side: heart badge or artwork peek
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isLiked) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Liked",
                        tint = MochyCoral,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (!coverUri.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurfaceElevated)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(coverUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                }
            }
        }
    }
}

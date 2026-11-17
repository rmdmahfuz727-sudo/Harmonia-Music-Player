package com.harmonia.music.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import com.harmonia.music.model.PlaybackState
import com.harmonia.music.model.Track
import com.harmonia.music.ui.components.FullPlayerSheet
import com.harmonia.music.ui.components.LocalSakugaVfx
import com.harmonia.music.ui.components.MiniPlayer
import com.harmonia.music.ui.components.MochyHopInCard
import com.harmonia.music.ui.components.SakugaVfxOverlay
import com.harmonia.music.ui.components.SakugaVfxState
import com.harmonia.music.ui.components.StreamItem
import com.harmonia.music.ui.components.TrackItem
import com.harmonia.music.ui.components.sakugaSquash
import com.harmonia.music.ui.theme.DarkBackground
import com.harmonia.music.ui.theme.DarkSurfaceElevated
import com.harmonia.music.ui.theme.DarkSurfaceVariant
import com.harmonia.music.ui.theme.MochyBackdropTop
import com.harmonia.music.ui.theme.MochyCoral
import com.harmonia.music.ui.theme.MochyCoralLight
import com.harmonia.music.ui.theme.MochyPillBackground
import com.harmonia.music.ui.theme.TextPrimary
import com.harmonia.music.ui.theme.TextSecondary
import com.harmonia.music.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onRequestPermission: () -> Unit
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val localTracks by viewModel.localTracks.collectAsState()
    val streams by viewModel.streams.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val hasPermission by viewModel.hasAudioPermission.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()

    var isPlayerExpanded by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    val mainVfxState = remember { SakugaVfxState() }
    val context = LocalContext.current

    CompositionLocalProvider(LocalSakugaVfx provides mainVfxState) {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                containerColor = DarkBackground,
                topBar = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkBackground)
                    ) {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Developer Avatar Squircle
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MochyCoral.copy(alpha = 0.25f))
                                            .clickable { showAboutDialog = true },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Mahfuz Rahman Profile",
                                            tint = MochyCoral,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = "Harmonia",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "By Mahfuz Rahman",
                                            fontSize = 11.sp,
                                            color = MochyCoral,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = { showAboutDialog = true },
                                    modifier = Modifier.sakugaSquash(squashAmount = 0.85f) {
                                        showAboutDialog = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "About Developer & Credits",
                                        tint = TextSecondary
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = DarkBackground,
                                titleContentColor = TextPrimary
                            )
                        )

                        // Mochy Capsule Search Bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            placeholder = {
                                Text(
                                    text = "Search tracks, artists, stations…",
                                    color = TextSecondary,
                                    fontSize = 13.5.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = MochyCoral
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear search",
                                            tint = TextSecondary
                                        )
                                    }
                                }
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MochyCoral,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                focusedContainerColor = MochyPillBackground,
                                unfocusedContainerColor = MochyPillBackground,
                                cursorColor = MochyCoral,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                },
                bottomBar = {
                    Column {
                        // Docked Mochy Mini Player
                        AnimatedVisibility(
                            visible = playbackState.currentTrack != null,
                            enter = slideInVertically(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                ),
                                initialOffsetY = { it }
                            ) + fadeIn(animationSpec = tween(200)),
                            exit = slideOutVertically(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessMedium
                                ),
                                targetOffsetY = { it }
                            ) + fadeOut(animationSpec = tween(150))
                        ) {
                            MiniPlayer(
                                playbackState = playbackState,
                                onPlayPauseClick = {
                                    mainVfxState.triggerRing(Offset(500f, 1800f), color = MochyCoral)
                                    viewModel.playerController.togglePlayPause()
                                },
                                onSkipNextClick = {
                                    mainVfxState.triggerSlash(Offset(700f, 1800f), color = MochyCoralLight)
                                    viewModel.playerController.skipNext()
                                },
                                onExpandClick = { isPlayerExpanded = true }
                            )
                        }

                        // Mochy Floating 4-Tab Navigation Bar
                        NavigationBar(
                            containerColor = MochyBackdropTop,
                            tonalElevation = 8.dp
                        ) {
                            NavigationBarItem(
                                selected = selectedTab == 0,
                                onClick = {
                                    viewModel.selectTab(0)
                                    mainVfxState.triggerMangaSfx("カチッ!", Offset(120f, 1900f), color = MochyCoral)
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = "Home",
                                        modifier = Modifier.sakugaSquash(squashAmount = 0.85f)
                                    )
                                },
                                label = { Text("Home", fontWeight = FontWeight.SemiBold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = MochyCoral,
                                    indicatorColor = MochyCoral,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                )
                            )

                            NavigationBarItem(
                                selected = selectedTab == 1,
                                onClick = {
                                    viewModel.selectTab(1)
                                    mainVfxState.triggerMangaSfx("カチッ!", Offset(320f, 1900f), color = MochyCoral)
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Radio,
                                        contentDescription = "Radio",
                                        modifier = Modifier.sakugaSquash(squashAmount = 0.85f)
                                    )
                                },
                                label = { Text("Radio", fontWeight = FontWeight.SemiBold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = MochyCoral,
                                    indicatorColor = MochyCoral,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                )
                            )

                            NavigationBarItem(
                                selected = selectedTab == 2,
                                onClick = {
                                    viewModel.selectTab(2)
                                    mainVfxState.triggerMangaSfx("カチッ!", Offset(520f, 1900f), color = MochyCoral)
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.LibraryMusic,
                                        contentDescription = "Library",
                                        modifier = Modifier.sakugaSquash(squashAmount = 0.85f)
                                    )
                                },
                                label = { Text("Library", fontWeight = FontWeight.SemiBold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = MochyCoral,
                                    indicatorColor = MochyCoral,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                )
                            )

                            NavigationBarItem(
                                selected = selectedTab == 3,
                                onClick = {
                                    viewModel.selectTab(3)
                                    mainVfxState.triggerMangaSfx("カチッ!", Offset(720f, 1900f), color = MochyCoral)
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.GraphicEq,
                                        contentDescription = "AudioLab",
                                        modifier = Modifier.sakugaSquash(squashAmount = 0.85f)
                                    )
                                },
                                label = { Text("AudioLab", fontWeight = FontWeight.SemiBold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = MochyCoral,
                                    indicatorColor = MochyCoral,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                )
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (selectedTab) {
                        0 -> MochyHomeScreen(
                            tracks = localTracks,
                            streams = streams,
                            favorites = favorites,
                            playbackState = playbackState,
                            onTrackClick = { track, list -> viewModel.playTrack(track, list) },
                            onShuffleAll = { list -> viewModel.shuffleAll(list) },
                            onFavoriteToggle = { track -> viewModel.toggleFavorite(track) },
                            onStreamClick = { stream -> viewModel.playSingleTrack(stream) }
                        )
                        1 -> OnlineStreamsScreen(
                            streams = streams,
                            playbackState = playbackState,
                            selectedGenre = selectedGenre,
                            onGenreSelect = { genre -> viewModel.selectGenre(genre) },
                            onStreamClick = { stream -> viewModel.playSingleTrack(stream) },
                            onFavoriteToggle = { stream -> viewModel.toggleFavorite(stream) },
                            onAddCustomStream = { name, url, genre -> viewModel.addCustomStream(name, url, genre) }
                        )
                        2 -> LocalTracksScreen(
                            tracks = localTracks,
                            playbackState = playbackState,
                            hasPermission = hasPermission,
                            onRequestPermission = onRequestPermission,
                            onTrackClick = { track, list -> viewModel.playTrack(track, list) },
                            onShuffleAll = { list -> viewModel.shuffleAll(list) },
                            onFavoriteToggle = { track -> viewModel.toggleFavorite(track) }
                        )
                        3 -> AudioLabScreen(
                            isPlaying = playbackState.isPlaying
                        )
                    }
                }
            }

            // Global Sakuga Anime VFX Overlay Layer
            SakugaVfxOverlay(vfxState = mainVfxState)
        }
    }

    // Full Player Modal Sheet
    if (isPlayerExpanded && playbackState.currentTrack != null) {
        FullPlayerSheet(
            playbackState = playbackState,
            onDismiss = { isPlayerExpanded = false },
            onPlayPauseClick = { viewModel.playerController.togglePlayPause() },
            onSeek = { targetMs -> viewModel.playerController.seekTo(targetMs) },
            onSkipNext = { viewModel.playerController.skipNext() },
            onSkipPrevious = { viewModel.playerController.skipPrevious() },
            onToggleShuffle = { viewModel.playerController.toggleShuffle() },
            onCycleRepeat = { viewModel.playerController.cycleRepeatMode() },
            onToggleFavorite = {
                playbackState.currentTrack?.let { viewModel.toggleFavorite(it) }
            }
        )
    }

    // Developer & Asset Attribution Credits Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = DarkSurfaceVariant,
            shape = RoundedCornerShape(26.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MochyCoral.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MochyCoral,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "About & Credits", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Lead Developer Card
                    Surface(
                        color = MochyPillBackground,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MochyCoral.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "LEAD DEVELOPER & CREATOR",
                                style = MaterialTheme.typography.labelSmall,
                                color = MochyCoral,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Mahfuz Rahman",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "@rmdmahfuz727-sudo",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MochyCoralLight,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "https://github.com/rmdmahfuz727-sudo",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    // Asset & Design Attribution Cards
                    Surface(
                        color = DarkSurfaceElevated,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "DESIGN & ASSET CREDITS",
                                style = MaterialTheme.typography.labelSmall,
                                color = MochyCoral,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Mochy-Music (by realidkroo)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Design language, flagship bottom sheet player, and aesthetic layout inspiration.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• Sakuga VFX (by finktheartist)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Anime keyframe spring physics, volume-preserving squash & stretch, and drawn VFX.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val browserIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/rmdmahfuz727-sudo")
                        )
                        context.startActivity(browserIntent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MochyCoral,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.sakugaSquash(squashAmount = 0.88f)
                ) {
                    Text("Visit Mahfuz Rahman's Profile", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAboutDialog = false },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Close", color = TextSecondary)
                }
            }
        )
    }
}

/**
 * Mochy-Music Signature Home Screen:
 *  - Hop-In 2-column grid of recent tracks
 *  - Live Broadcast Highlights
 *  - Quick play recommendations
 */
@Composable
private fun MochyHomeScreen(
    tracks: List<Track>,
    streams: List<Track>,
    favorites: List<Track>,
    playbackState: PlaybackState,
    onTrackClick: (Track, List<Track>) -> Unit,
    onShuffleAll: (List<Track>) -> Unit,
    onFavoriteToggle: (Track) -> Unit,
    onStreamClick: (Track) -> Unit
) {
    val hopInTracks = (favorites.ifEmpty { tracks }).take(4)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Hop-In Section
        if (hopInTracks.isNotEmpty()) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hop In",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )

                        Text(
                            text = "${tracks.size} songs available",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        hopInTracks.forEach { track ->
                            MochyHopInCard(
                                track = track,
                                isPlaying = playbackState.currentTrack?.id == track.id && playbackState.isPlaying,
                                onClick = { onTrackClick(track, tracks) }
                            )
                        }
                    }
                }
            }
        }

        // Curated Live Broadcasts Section
        if (streams.isNotEmpty()) {
            item {
                Column {
                    Text(
                        text = "Curated Broadcasts",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        streams.take(5).forEach { stream ->
                            Box(modifier = Modifier.width(280.dp)) {
                                StreamItem(
                                    stream = stream,
                                    isCurrentTrack = playbackState.currentTrack?.id == stream.id,
                                    isPlaying = playbackState.isPlaying,
                                    onStreamClick = { onStreamClick(stream) },
                                    onFavoriteToggle = { onFavoriteToggle(stream) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Offline Library Tracks Preview
        if (tracks.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Your Songs",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Surface(
                        color = MochyCoral,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.sakugaSquash(squashAmount = 0.88f) { onShuffleAll(tracks) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = "Shuffle",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Shuffle",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            items(tracks.take(8), key = { it.id }) { track ->
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
}

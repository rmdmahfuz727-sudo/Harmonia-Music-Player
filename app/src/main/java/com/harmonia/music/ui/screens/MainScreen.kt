package com.harmonia.music.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import com.harmonia.music.ui.components.FullPlayerSheet
import com.harmonia.music.ui.components.MiniPlayer
import com.harmonia.music.ui.theme.AccentCyan
import com.harmonia.music.ui.theme.AccentPurple
import com.harmonia.music.ui.theme.DarkBackground
import com.harmonia.music.ui.theme.DarkSurfaceElevated
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
                        Text(
                            text = "Harmonia",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = AccentCyan
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkBackground,
                        titleContentColor = AccentCyan
                    )
                )

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = if (selectedTab == 1) "Search stations or genres…" else "Search tracks, artists…",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextSecondary
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
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = DarkSurfaceElevated,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        cursorColor = AccentCyan
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
                // Docked Mini Player
                AnimatedVisibility(
                    visible = playbackState.currentTrack != null,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    MiniPlayer(
                        playbackState = playbackState,
                        onPlayPauseClick = { viewModel.playerController.togglePlayPause() },
                        onSkipNextClick = { viewModel.playerController.skipNext() },
                        onExpandClick = { isPlayerExpanded = true }
                    )
                }

                // Bottom Navigation
                NavigationBar(
                    containerColor = DarkBackground,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { viewModel.selectTab(0) },
                        icon = {
                            Icon(Icons.Default.LibraryMusic, contentDescription = "Local Music")
                        },
                        label = { Text("Local Music") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkBackground,
                            selectedTextColor = AccentCyan,
                            indicatorColor = AccentCyan,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )

                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { viewModel.selectTab(1) },
                        icon = {
                            Icon(Icons.Default.Radio, contentDescription = "Live Streams")
                        },
                        label = { Text("Live Streams") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkBackground,
                            selectedTextColor = AccentCyan,
                            indicatorColor = AccentCyan,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )

                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { viewModel.selectTab(2) },
                        icon = {
                            Icon(Icons.Default.Favorite, contentDescription = "Favorites")
                        },
                        label = { Text("Favorites") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkBackground,
                            selectedTextColor = AccentCyan,
                            indicatorColor = AccentCyan,
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
                0 -> LocalTracksScreen(
                    tracks = localTracks,
                    playbackState = playbackState,
                    hasPermission = hasPermission,
                    onRequestPermission = onRequestPermission,
                    onTrackClick = { track, list -> viewModel.playTrack(track, list) },
                    onShuffleAll = { list -> viewModel.shuffleAll(list) },
                    onFavoriteToggle = { track -> viewModel.toggleFavorite(track) }
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
                2 -> FavoritesScreen(
                    favorites = favorites,
                    playbackState = playbackState,
                    onTrackClick = { track, list -> viewModel.playTrack(track, list) },
                    onShuffleFavorites = { list -> viewModel.shuffleAll(list) },
                    onFavoriteToggle = { track -> viewModel.toggleFavorite(track) }
                )
            }
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
}

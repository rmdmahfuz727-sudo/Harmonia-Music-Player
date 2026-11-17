package com.harmonia.music.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.harmonia.music.ui.components.MochyBottomBar
import com.harmonia.music.ui.components.SakugaVfxOverlay
import com.harmonia.music.ui.components.SakugaVfxState
import com.harmonia.music.ui.components.sakugaSquash
import com.harmonia.music.ui.theme.DarkSurfaceElevated
import com.harmonia.music.ui.theme.DarkSurfaceVariant
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
    val favorites by viewModel.favorites.collectAsState()
    val hasPermission by viewModel.hasAudioPermission.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()
    val ytSearchResults by viewModel.ytSearchResults.collectAsState()
    val ytTrending by viewModel.ytTrending.collectAsState()
    val isSearchingYT by viewModel.isSearchingYT.collectAsState()

    var isPlayerExpanded by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showAudioLabSheet by remember { mutableStateOf(false) }
    val mainVfxState = remember { SakugaVfxState() }
    val context = LocalContext.current

    CompositionLocalProvider(LocalSakugaVfx provides mainVfxState) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0A0C))
        ) {
            // Main Content Area with Smooth Tab Transitions
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        val direction = if (targetState > initialState) 1 else -1
                        (slideInHorizontally(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            initialOffsetX = { direction * it / 3 }
                        ) + fadeIn(animationSpec = tween(220)))
                            .togetherWith(
                                slideOutHorizontally(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioNoBouncy,
                                        stiffness = Spring.StiffnessMediumLow
                                    ),
                                    targetOffsetX = { -direction * it / 3 }
                                ) + fadeOut(animationSpec = tween(180))
                            )
                    },
                    label = "tab_transition"
                ) { targetTab ->
                    when (targetTab) {
                        0 -> HomeScreen(
                            tracks = localTracks,
                            trendingTracks = ytTrending,
                            favorites = favorites,
                            playbackState = playbackState,
                            onTrackClick = { track, list -> viewModel.playTrack(track, list) },
                            onShuffleAll = { list -> viewModel.shuffleAll(list) },
                            onFavoriteToggle = { track -> viewModel.toggleFavorite(track) },
                            onYtTrackClick = { track -> viewModel.playYtTrack(track) },
                            onOpenProfile = { viewModel.selectTab(3) }
                        )

                        1 -> SearchScreen(
                            searchQuery = searchQuery,
                            searchResults = ytSearchResults,
                            isSearching = isSearchingYT,
                            playbackState = playbackState,
                            onQueryChange = { viewModel.updateSearchQuery(it) },
                            onTrackClick = { track -> viewModel.playYtTrack(track) },
                            onOpenProfile = { viewModel.selectTab(3) }
                        )

                        2 -> LibraryScreen(
                            tracks = localTracks,
                            favorites = favorites,
                            playbackState = playbackState,
                            hasPermission = hasPermission,
                            onRequestPermission = onRequestPermission,
                            onTrackClick = { track, list -> viewModel.playTrack(track, list) },
                            onFavoriteToggle = { track -> viewModel.toggleFavorite(track) },
                            onOpenProfile = { viewModel.selectTab(3) }
                        )

                        3 -> ProfileScreen(
                            onOpenAudioLab = { showAudioLabSheet = true },
                            onShowAboutDialog = { showAboutDialog = true }
                        )
                    }
                }
            }

            // Bottom Floating Controls Layer: Mini Player + Mochy Floating Dock
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                // Docked Mini Player
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

                // Mochy Floating 4-Tab Capsule Dock
                MochyBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { tabIndex -> viewModel.selectTab(tabIndex) }
                )
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

    // Audio Lab Bottom Sheet
    if (showAudioLabSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAudioLabSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color(0xFF141418),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AudioLabScreen(isPlaying = playbackState.isPlaying)
            }
        }
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

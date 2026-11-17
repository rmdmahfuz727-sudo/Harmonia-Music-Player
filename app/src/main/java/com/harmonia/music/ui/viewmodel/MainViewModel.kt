package com.harmonia.music.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.harmonia.music.data.MediaRepository
import com.harmonia.music.model.PlaybackState
import com.harmonia.music.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@UnstableApi
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MediaRepository(application)
    val playerController = MusicPlayerController(application, viewModelScope, repository)

    val playbackState: StateFlow<PlaybackState> = playerController.playbackState

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _hasAudioPermission = MutableStateFlow(false)
    val hasAudioPermission: StateFlow<Boolean> = _hasAudioPermission.asStateFlow()

    // YouTube Music state
    private val _isSearchingYT = MutableStateFlow(false)
    val isSearchingYT: StateFlow<Boolean> = _isSearchingYT.asStateFlow()

    private val _isLoadingTrending = MutableStateFlow(false)
    val isLoadingTrending: StateFlow<Boolean> = _isLoadingTrending.asStateFlow()

    val ytSearchResults: StateFlow<List<Track>> = repository.ytSearchResults
    val ytTrending: StateFlow<List<Track>> = repository.ytTrending

    val localTracks: StateFlow<List<Track>> = combine(
        repository.localTracks,
        _searchQuery
    ) { tracks, query ->
        if (query.isBlank()) {
            tracks
        } else {
            tracks.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true) ||
                it.album.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<Track>> = combine(
        repository.favorites,
        _searchQuery
    ) { favs, query ->
        if (query.isBlank()) {
            favs
        } else {
            favs.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Load trending music on startup
        viewModelScope.launch {
            _isLoadingTrending.value = true
            try {
                repository.refreshYtTrending()
            } catch (_: Exception) { }
            _isLoadingTrending.value = false
        }
    }

    fun onAudioPermissionResult(isGranted: Boolean) {
        _hasAudioPermission.value = isGranted
        if (isGranted) {
            viewModelScope.launch {
                repository.refreshLocalTracks()
            }
        }
    }

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        // If on YT Music tab and query changes, trigger YT search
        if (_selectedTab.value == 1 && query.isNotBlank()) {
            searchYouTubeMusic(query)
        } else if (query.isBlank()) {
            repository.clearYtSearch()
        }
    }

    fun searchYouTubeMusic(query: String) {
        viewModelScope.launch {
            _isSearchingYT.value = true
            try {
                repository.searchYouTubeMusic(query)
            } catch (_: Exception) { }
            _isSearchingYT.value = false
        }
    }

    fun searchYtByMood(mood: String) {
        _searchQuery.value = mood
        searchYouTubeMusic(mood)
    }

    fun refreshData() {
        viewModelScope.launch {
            if (_hasAudioPermission.value) {
                repository.refreshLocalTracks()
            }
        }
    }

    fun playTrack(track: Track, queue: List<Track>) {
        val index = queue.indexOfFirst { it.id == track.id }.coerceAtLeast(0)
        playerController.playQueue(queue, index)
    }

    fun playSingleTrack(track: Track) {
        playerController.playSingle(track)
    }

    fun playYtTrack(track: Track) {
        // For YT tracks, resolve stream URL first then play
        playerController.playSingle(track)
    }

    fun shuffleAll(tracks: List<Track>) {
        if (tracks.isEmpty()) return
        val shuffled = tracks.shuffled()
        playerController.playQueue(shuffled, 0)
    }

    fun toggleFavorite(track: Track) {
        repository.toggleFavorite(track)
    }

    override fun onCleared() {
        super.onCleared()
        playerController.release()
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(app) as T
        }
    }
}

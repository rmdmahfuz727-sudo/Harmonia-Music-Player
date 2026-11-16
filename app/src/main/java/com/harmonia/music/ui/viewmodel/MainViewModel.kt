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
    val playerController = MusicPlayerController(application, viewModelScope)

    val playbackState: StateFlow<PlaybackState> = playerController.playbackState

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGenre = MutableStateFlow<String?>(null)
    val selectedGenre: StateFlow<String?> = _selectedGenre.asStateFlow()

    private val _hasAudioPermission = MutableStateFlow(false)
    val hasAudioPermission: StateFlow<Boolean> = _hasAudioPermission.asStateFlow()

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

    val streams: StateFlow<List<Track>> = combine(
        repository.streams,
        _searchQuery,
        _selectedGenre
    ) { streams, query, genre ->
        var list = streams
        if (!genre.isNullOrBlank()) {
            list = list.filter { it.streamGenre?.equals(genre, ignoreCase = true) == true }
        }
        if (query.isNotBlank()) {
            list = list.filter {
                it.title.contains(query, ignoreCase = true) ||
                (it.streamGenre?.contains(query, ignoreCase = true) == true)
            }
        }
        list
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
        viewModelScope.launch {
            repository.refreshStreams()
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
    }

    fun selectGenre(genre: String?) {
        _selectedGenre.value = if (_selectedGenre.value == genre) null else genre
    }

    fun refreshData() {
        viewModelScope.launch {
            if (_hasAudioPermission.value) {
                repository.refreshLocalTracks()
            }
            repository.refreshStreams()
        }
    }

    fun playTrack(track: Track, queue: List<Track>) {
        val index = queue.indexOfFirst { it.id == track.id }.coerceAtLeast(0)
        playerController.playQueue(queue, index)
    }

    fun playSingleTrack(track: Track) {
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

    fun addCustomStream(name: String, url: String, genre: String) {
        viewModelScope.launch {
            repository.addCustomStream(name, url, genre)
        }
    }

    fun removeCustomStream(url: String) {
        viewModelScope.launch {
            repository.removeCustomStream(url)
        }
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

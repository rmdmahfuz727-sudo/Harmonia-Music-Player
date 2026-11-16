package com.harmonia.music.data

import android.content.Context
import android.content.SharedPreferences
import com.harmonia.music.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MediaRepository(private val context: Context) {

    private val localSource = LocalAudioDataSource(context)
    private val streamSource = OnlineStreamDataSource(context)
    private val favPrefs: SharedPreferences =
        context.getSharedPreferences("harmonia_favorites", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FAVORITES = "favorite_ids"
    }

    private val _localTracks = MutableStateFlow<List<Track>>(emptyList())
    val localTracks: StateFlow<List<Track>> = _localTracks.asStateFlow()

    private val _streams = MutableStateFlow<List<Track>>(emptyList())
    val streams: StateFlow<List<Track>> = _streams.asStateFlow()

    private val _favorites = MutableStateFlow<List<Track>>(emptyList())
    val favorites: StateFlow<List<Track>> = _favorites.asStateFlow()

    suspend fun refreshLocalTracks() {
        val favIds = getFavoriteIds()
        val tracks = localSource.getLocalTracks().map { track ->
            track.copy(isFavorite = favIds.contains(track.id))
        }
        _localTracks.value = tracks
        updateFavoritesList()
    }

    suspend fun refreshStreams() {
        val favIds = getFavoriteIds()
        val streamTracks = streamSource.getStreams().map { track ->
            track.copy(isFavorite = favIds.contains(track.id))
        }
        _streams.value = streamTracks
        updateFavoritesList()
    }

    suspend fun addCustomStream(name: String, url: String, genre: String): Track {
        val created = streamSource.addCustomStream(name, url, genre)
        refreshStreams()
        return created
    }

    suspend fun removeCustomStream(url: String) {
        streamSource.removeCustomStream(url)
        refreshStreams()
    }

    fun toggleFavorite(track: Track): Boolean {
        val favIds = getFavoriteIds().toMutableSet()
        val willBeFav = if (favIds.contains(track.id)) {
            favIds.remove(track.id)
            false
        } else {
            favIds.add(track.id)
            true
        }
        favPrefs.edit().putStringSet(KEY_FAVORITES, favIds).apply()

        // Update local tracks state
        _localTracks.value = _localTracks.value.map {
            if (it.id == track.id) it.copy(isFavorite = willBeFav) else it
        }

        // Update streams state
        _streams.value = _streams.value.map {
            if (it.id == track.id) it.copy(isFavorite = willBeFav) else it
        }

        updateFavoritesList()
        return willBeFav
    }

    private fun getFavoriteIds(): Set<String> {
        return favPrefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    private fun updateFavoritesList() {
        val favIds = getFavoriteIds()
        val allTracks = _localTracks.value + _streams.value
        _favorites.value = allTracks.filter { favIds.contains(it.id) }.distinctBy { it.id }
    }
}

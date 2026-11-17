package com.harmonia.music.data

import android.content.Context
import android.content.SharedPreferences
import com.harmonia.music.model.Playlist
import com.harmonia.music.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MediaRepository(private val context: Context) {

    private val localSource = LocalAudioDataSource(context)
    val ytClient = YouTubeMusicClient()
    val lyricsClient = LrcLibClient()
    val playlistManager = PlaylistManager(context)

    private val favPrefs: SharedPreferences =
        context.getSharedPreferences("harmonia_favorites", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FAVORITES = "favorite_ids"
    }

    private val _localTracks = MutableStateFlow<List<Track>>(emptyList())
    val localTracks: StateFlow<List<Track>> = _localTracks.asStateFlow()

    private val _ytSearchResults = MutableStateFlow<List<Track>>(emptyList())
    val ytSearchResults: StateFlow<List<Track>> = _ytSearchResults.asStateFlow()

    private val _ytTrending = MutableStateFlow<List<Track>>(emptyList())
    val ytTrending: StateFlow<List<Track>> = _ytTrending.asStateFlow()

    private val _favorites = MutableStateFlow<List<Track>>(emptyList())
    val favorites: StateFlow<List<Track>> = _favorites.asStateFlow()

    val playlists: StateFlow<List<Playlist>> = playlistManager.playlists

    suspend fun refreshLocalTracks() {
        val favIds = getFavoriteIds()
        val tracks = localSource.getLocalTracks().map { track ->
            track.copy(isFavorite = favIds.contains(track.id))
        }
        _localTracks.value = tracks
        updateFavoritesList()
    }

    suspend fun searchYouTubeMusic(query: String): List<Track> {
        val results = ytClient.search(query)
        _ytSearchResults.value = results
        return results
    }

    suspend fun refreshYtTrending() {
        val trending = ytClient.getTrending()
        _ytTrending.value = trending
    }

    suspend fun resolveYtStreamUrl(videoId: String): String? {
        return ytClient.resolveStreamUrl(videoId)
    }

    suspend fun fetchLyrics(track: Track): LyricsResult? {
        val durationSec = track.durationMs / 1000
        return lyricsClient.getLyrics(track.title, track.artist, track.album, durationSec)
    }

    fun clearYtSearch() {
        _ytSearchResults.value = emptyList()
    }

    fun createPlaylist(name: String): Playlist {
        return playlistManager.createPlaylist(name)
    }

    fun deletePlaylist(playlistId: String) {
        playlistManager.deletePlaylist(playlistId)
    }

    fun addTrackToPlaylist(playlistId: String, trackId: String) {
        playlistManager.addTrackToPlaylist(playlistId, trackId)
    }

    fun removeTrackFromPlaylist(playlistId: String, trackId: String) {
        playlistManager.removeTrackFromPlaylist(playlistId, trackId)
    }

    fun renamePlaylist(playlistId: String, newName: String) {
        playlistManager.renamePlaylist(playlistId, newName)
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

        updateFavoritesList()
        return willBeFav
    }

    private fun getFavoriteIds(): Set<String> {
        return favPrefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    private fun updateFavoritesList() {
        val favIds = getFavoriteIds()
        val allTracks = _localTracks.value
        _favorites.value = allTracks.filter { favIds.contains(it.id) }.distinctBy { it.id }
    }
}

package com.harmonia.music.data

import android.content.Context
import android.content.SharedPreferences
import com.harmonia.music.model.Playlist
import com.harmonia.music.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * Manages persistent playlists, custom user creations, and smart playlists.
 */
class PlaylistManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("harmonia_playlists", Context.MODE_PRIVATE)

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    init {
        loadPlaylists()
    }

    private fun loadPlaylists() {
        val raw = prefs.getString("user_playlists", null)
        val list = mutableListOf<Playlist>()
        if (!raw.isNullOrBlank()) {
            try {
                val array = JSONArray(raw)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val trackArray = obj.optJSONArray("trackIds")
                    val trackIds = mutableListOf<String>()
                    if (trackArray != null) {
                        for (j in 0 until trackArray.length()) {
                            trackIds.add(trackArray.getString(j))
                        }
                    }
                    list.add(
                        Playlist(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            trackIds = trackIds,
                            createdAtMs = obj.optLong("createdAtMs", System.currentTimeMillis()),
                            artworkUri = obj.optString("artworkUri", null)
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Add default smart playlists if empty
        if (list.isEmpty()) {
            list.add(
                Playlist(
                    id = "smart_favorites",
                    name = "Favorites",
                    trackIds = emptyList(),
                    isSmart = true
                )
            )
            list.add(
                Playlist(
                    id = "smart_heavy_rotation",
                    name = "Heavy Rotation",
                    trackIds = emptyList(),
                    isSmart = true
                )
            )
        }

        _playlists.value = list
    }

    private fun savePlaylists(list: List<Playlist>) {
        val array = JSONArray()
        for (pl in list.filter { !it.isSmart }) {
            val obj = JSONObject().apply {
                put("id", pl.id)
                put("name", pl.name)
                put("createdAtMs", pl.createdAtMs)
                pl.artworkUri?.let { put("artworkUri", it) }
                val trackArray = JSONArray()
                pl.trackIds.forEach { trackArray.put(it) }
                put("trackIds", trackArray)
            }
            array.put(obj)
        }
        prefs.edit().putString("user_playlists", array.toString()).apply()
        _playlists.value = list
    }

    fun createPlaylist(name: String): Playlist {
        val newPl = Playlist(
            id = UUID.randomUUID().toString(),
            name = name.trim().ifBlank { "New Playlist" },
            trackIds = emptyList(),
            createdAtMs = System.currentTimeMillis()
        )
        val updated = _playlists.value + newPl
        savePlaylists(updated)
        return newPl
    }

    fun deletePlaylist(playlistId: String) {
        val updated = _playlists.value.filter { it.id != playlistId }
        savePlaylists(updated)
    }

    fun addTrackToPlaylist(playlistId: String, trackId: String) {
        val updated = _playlists.value.map { pl ->
            if (pl.id == playlistId && !pl.trackIds.contains(trackId)) {
                pl.copy(trackIds = pl.trackIds + trackId)
            } else pl
        }
        savePlaylists(updated)
    }

    fun removeTrackFromPlaylist(playlistId: String, trackId: String) {
        val updated = _playlists.value.map { pl ->
            if (pl.id == playlistId) {
                pl.copy(trackIds = pl.trackIds.filter { it != trackId })
            } else pl
        }
        savePlaylists(updated)
    }

    fun renamePlaylist(playlistId: String, newName: String) {
        val updated = _playlists.value.map { pl ->
            if (pl.id == playlistId) pl.copy(name = newName.trim()) else pl
        }
        savePlaylists(updated)
    }
}

package com.harmonia.music.data

import android.content.Context
import android.content.SharedPreferences
import com.harmonia.music.model.StreamPreset
import com.harmonia.music.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class OnlineStreamDataSource(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("harmonia_streams", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CUSTOM_STREAMS = "custom_streams"
    }

    suspend fun getStreams(): List<Track> = withContext(Dispatchers.IO) {
        val tracks = mutableListOf<Track>()

        // 1. Load curated presets
        for (preset in StreamPreset.defaultPresets) {
            tracks.add(preset.toTrack())
        }

        // 2. Load custom streams saved by the user
        val customJson = prefs.getString(KEY_CUSTOM_STREAMS, null)
        if (!customJson.isNullOrEmpty()) {
            try {
                val array = JSONArray(customJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val name = obj.getString("name")
                    val url = obj.getString("url")
                    val genre = obj.optString("genre", "Custom Stream")
                    tracks.add(
                        Track(
                            id = "custom_stream_${name.hashCode()}_$i",
                            title = name,
                            artist = genre,
                            album = "Online Stream",
                            durationMs = 0L,
                            mediaUri = url,
                            isStream = true,
                            streamGenre = genre
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        tracks
    }

    suspend fun addCustomStream(name: String, url: String, genre: String): Track = withContext(Dispatchers.IO) {
        val customJson = prefs.getString(KEY_CUSTOM_STREAMS, "[]")
        val array = JSONArray(customJson)
        val newObj = JSONObject().apply {
            put("name", name)
            put("url", url)
            put("genre", genre)
        }
        array.put(newObj)
        prefs.edit().putString(KEY_CUSTOM_STREAMS, array.toString()).apply()

        Track(
            id = "custom_stream_${name.hashCode()}_${array.length() - 1}",
            title = name,
            artist = genre,
            album = "Online Stream",
            durationMs = 0L,
            mediaUri = url,
            isStream = true,
            streamGenre = genre
        )
    }

    suspend fun removeCustomStream(mediaUri: String) = withContext(Dispatchers.IO) {
        val customJson = prefs.getString(KEY_CUSTOM_STREAMS, null) ?: return@withContext
        try {
            val array = JSONArray(customJson)
            val newArray = JSONArray()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                if (obj.getString("url") != mediaUri) {
                    newArray.put(obj)
                }
            }
            prefs.edit().putString(KEY_CUSTOM_STREAMS, newArray.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

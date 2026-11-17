package com.harmonia.music.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.regex.Pattern

data class LyricLine(
    val timeMs: Long,
    val text: String
)

data class LyricsResult(
    val id: Long = 0,
    val trackName: String = "",
    val artistName: String = "",
    val albumName: String = "",
    val durationSeconds: Double = 0.0,
    val plainLyrics: String? = null,
    val syncedLyrics: List<LyricLine> = emptyList(),
    val isSynced: Boolean = false
)

/**
 * LRCLIB open-source lyrics client.
 * Fetches time-synchronized lyrics from the official, open LRCLIB database (no API key required).
 */
class LrcLibClient {

    companion object {
        private const val API_BASE = "https://lrclib.net/api/get"
        private const val TIMEOUT_MS = 8_000
        private val LRC_TIMESTAMP_PATTERN = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{2,3})\\]")
    }

    suspend fun getLyrics(trackName: String, artistName: String, albumName: String = "", durationSeconds: Long = 0): LyricsResult? = withContext(Dispatchers.IO) {
        try {
            val queryParams = mutableListOf<String>()
            queryParams.add("track_name=" + URLEncoder.encode(cleanTitle(trackName), "UTF-8"))
            if (artistName.isNotBlank() && artistName != "Unknown Artist") {
                queryParams.add("artist_name=" + URLEncoder.encode(cleanArtist(artistName), "UTF-8"))
            }
            if (albumName.isNotBlank()) {
                queryParams.add("album_name=" + URLEncoder.encode(albumName, "UTF-8"))
            }
            if (durationSeconds > 0) {
                queryParams.add("duration=$durationSeconds")
            }

            val urlStr = "$API_BASE?" + queryParams.joinToString("&")
            val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = TIMEOUT_MS
                readTimeout = TIMEOUT_MS
                setRequestProperty("User-Agent", "HarmoniaMusicPlayer/1.1 (https://github.com/rmdmahfuz727-sudo)")
            }

            if (conn.responseCode != 200) {
                return@withContext null
            }

            val response = BufferedReader(InputStreamReader(conn.inputStream, Charsets.UTF_8)).use {
                it.readText()
            }

            val json = JSONObject(response)
            val synced = json.optString("syncedLyrics", null)
            val plain = json.optString("plainLyrics", null)

            val parsedSynced = if (!synced.isNullOrBlank()) parseLrc(synced) else emptyList()

            LyricsResult(
                id = json.optLong("id", 0),
                trackName = json.optString("trackName", trackName),
                artistName = json.optString("artistName", artistName),
                albumName = json.optString("albumName", albumName),
                durationSeconds = json.optDouble("duration", 0.0),
                plainLyrics = plain,
                syncedLyrics = parsedSynced,
                isSynced = parsedSynced.isNotEmpty()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseLrc(lrcText: String): List<LyricLine> {
        val lines = mutableListOf<LyricLine>()
        val rawLines = lrcText.split("\n")
        for (raw in rawLines) {
            val trimmed = raw.trim()
            if (trimmed.isEmpty()) continue
            val matcher = LRC_TIMESTAMP_PATTERN.matcher(trimmed)
            if (matcher.find()) {
                val min = matcher.group(1)?.toLongOrNull() ?: 0L
                val sec = matcher.group(2)?.toLongOrNull() ?: 0L
                val msStr = matcher.group(3) ?: "0"
                val ms = if (msStr.length == 2) msStr.toLong() * 10 else msStr.toLong()
                val totalMs = (min * 60 + sec) * 1000 + ms
                val text = trimmed.substring(matcher.end()).trim()
                if (text.isNotBlank()) {
                    lines.add(LyricLine(timeMs = totalMs, text = text))
                }
            }
        }
        return lines.sortedBy { it.timeMs }
    }

    private fun cleanTitle(title: String): String {
        return title
            .replace(Regex("(?i)\\[.*?\\]|\\(.*?\\)"), "") // remove [Official Video], (Remix)
            .replace(Regex("(?i)ft\\..*|feat\\..*"), "")
            .trim()
    }

    private fun cleanArtist(artist: String): String {
        return artist.split(",", "&", "feat.", "ft.")[0].trim()
    }
}

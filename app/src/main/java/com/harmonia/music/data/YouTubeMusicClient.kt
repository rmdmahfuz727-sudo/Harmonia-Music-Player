package com.harmonia.music.data

import com.harmonia.music.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * Lightweight YouTube Music client using the public InnerTube API (WEB_REMIX)
 * and Piped/Invidious for direct audio stream resolution.
 *
 * No API key required — uses the same public context that the YouTube Music
 * web app sends from the browser.
 */
class YouTubeMusicClient {

    companion object {
        private const val INNERTUBE_SEARCH_URL =
            "https://music.youtube.com/youtubei/v1/search?prettyPrint=false"

        // Redundant Invidious & Piped backend instances
        private val STREAM_INSTANCES = listOf(
            "https://yt.chocolatemoo53.com",
            "https://pipedapi.kavin.rocks",
            "https://api.piped.privacydev.net",
            "https://invidious.projectsegfau.lt"
        )

        private const val CONNECT_TIMEOUT = 10_000
        private const val READ_TIMEOUT = 12_000
    }

    private fun buildInnerTubeContext(): JSONObject {
        return JSONObject().apply {
            put("context", JSONObject().apply {
                put("client", JSONObject().apply {
                    put("clientName", "WEB_REMIX")
                    put("clientVersion", "1.20231204.01.00")
                    put("hl", "en")
                    put("gl", "US")
                })
            })
        }
    }

    /**
     * Search YouTube Music for tracks matching [query].
     * Returns parsed Track objects with videoId, title, artist, thumbnail, and duration.
     */
    suspend fun search(query: String): List<Track> = withContext(Dispatchers.IO) {
        try {
            val body = buildInnerTubeContext().apply {
                put("query", query)
                put("params", "EgWKAQIIAWoOEAMQBBAJEAoQBRAREBU=") // filter: songs
            }

            val responseText = httpPost(INNERTUBE_SEARCH_URL, body.toString())
            parseSearchResults(responseText)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Get trending / popular music tracks.
     * Uses a broad search for popular music as a lightweight alternative
     * to the browse endpoint.
     */
    suspend fun getTrending(): List<Track> = withContext(Dispatchers.IO) {
        try {
            val body = buildInnerTubeContext().apply {
                put("query", "trending music 2024")
                put("params", "EgWKAQIIAWoOEAMQBBAJEAoQBRAREBU=")
            }
            val responseText = httpPost(INNERTUBE_SEARCH_URL, body.toString())
            parseSearchResults(responseText)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Resolve a playable audio stream URL for the given [videoId]
     * using Invidious and Piped instances with automatic failover.
     */
    suspend fun resolveStreamUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        for (instance in STREAM_INSTANCES) {
            try {
                // 1. Try Invidious API
                val invidiousUrl = "$instance/api/v1/videos/$videoId"
                val responseText = httpGet(invidiousUrl)
                val json = JSONObject(responseText)
                val formats = json.optJSONArray("adaptiveFormats")
                if (formats != null && formats.length() > 0) {
                    var bestUrl: String? = null
                    var bestBitrate = 0
                    for (i in 0 until formats.length()) {
                        val fmt = formats.getJSONObject(i)
                        val type = fmt.optString("type", "")
                        val url = fmt.optString("url", "")
                        val bitrate = fmt.optInt("bitrate", 0)
                        if (type.contains("audio") && url.isNotBlank() && bitrate > bestBitrate) {
                            bestBitrate = bitrate
                            bestUrl = url
                        }
                    }
                    if (bestUrl != null) return@withContext bestUrl
                }

                // 2. Try Piped API
                val pipedStreams = json.optJSONArray("audioStreams")
                if (pipedStreams != null && pipedStreams.length() > 0) {
                    var bestUrl: String? = null
                    var bestBitrate = 0
                    for (i in 0 until pipedStreams.length()) {
                        val stream = pipedStreams.getJSONObject(i)
                        val bitrate = stream.optInt("bitrate", 0)
                        val streamUrl = stream.optString("url", "")
                        if (bitrate > bestBitrate && streamUrl.isNotEmpty()) {
                            bestBitrate = bitrate
                            bestUrl = streamUrl
                        }
                    }
                    if (bestUrl != null) return@withContext bestUrl
                }
            } catch (e: Exception) {
                // Try next endpoint
            }
        }
        null
    }

    // ── InnerTube response parser ────────────────────────────────────────

    private fun parseSearchResults(responseText: String): List<Track> {
        val tracks = mutableListOf<Track>()
        try {
            val root = JSONObject(responseText)
            val contents = root
                .optJSONObject("contents")
                ?.optJSONObject("tabbedSearchResultsRenderer")
                ?.optJSONArray("tabs")
                ?.optJSONObject(0)
                ?.optJSONObject("tabRenderer")
                ?.optJSONObject("content")
                ?.optJSONObject("sectionListRenderer")
                ?.optJSONArray("contents")
                ?: return emptyList()

            for (s in 0 until contents.length()) {
                val section = contents.optJSONObject(s) ?: continue
                val musicItems = section
                    .optJSONObject("musicShelfRenderer")
                    ?.optJSONArray("contents")
                    ?: continue

                for (i in 0 until musicItems.length()) {
                    val item = musicItems.optJSONObject(i) ?: continue
                    val renderer = item.optJSONObject("musicResponsiveListItemRenderer") ?: continue
                    val track = parseMusicItem(renderer)
                    if (track != null) tracks.add(track)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return tracks
    }

    private fun parseMusicItem(renderer: JSONObject): Track? {
        try {
            // Extract videoId from playlistItemData or overlay
            val videoId = renderer
                .optJSONObject("playlistItemData")
                ?.optString("videoId")
                ?: renderer
                    .optJSONObject("overlay")
                    ?.optJSONObject("musicItemThumbnailOverlayRenderer")
                    ?.optJSONObject("content")
                    ?.optJSONObject("musicPlayButtonRenderer")
                    ?.optJSONObject("playNavigationEndpoint")
                    ?.optJSONObject("watchEndpoint")
                    ?.optString("videoId")
                ?: return null

            if (videoId.isBlank()) return null

            // Extract title and artist from flexColumns
            val flexColumns = renderer.optJSONArray("flexColumns") ?: return null

            val title = flexColumns.optJSONObject(0)
                ?.optJSONObject("musicResponsiveListItemFlexColumnRenderer")
                ?.optJSONObject("text")
                ?.optJSONArray("runs")
                ?.optJSONObject(0)
                ?.optString("text")
                ?: "Unknown Title"

            var artist = "Unknown Artist"
            var album = ""
            var durationText = ""

            if (flexColumns.length() > 1) {
                val secondCol = flexColumns.optJSONObject(1)
                    ?.optJSONObject("musicResponsiveListItemFlexColumnRenderer")
                    ?.optJSONObject("text")
                    ?.optJSONArray("runs")

                if (secondCol != null) {
                    // Usually: Artist • Album • Duration
                    val parts = mutableListOf<String>()
                    for (r in 0 until secondCol.length()) {
                        val text = secondCol.optJSONObject(r)?.optString("text") ?: ""
                        if (text != " \u2022 " && text.isNotBlank()) parts.add(text)
                    }
                    if (parts.isNotEmpty()) artist = parts[0]
                    if (parts.size > 1) album = parts[1]
                    if (parts.size > 2) durationText = parts.last()
                }
            }

            // Parse duration text (e.g. "3:45") to milliseconds
            val durationMs = parseDurationToMs(durationText)

            // Thumbnail
            val thumbnail = renderer
                .optJSONObject("thumbnail")
                ?.optJSONObject("musicThumbnailRenderer")
                ?.optJSONObject("thumbnail")
                ?.optJSONArray("thumbnails")
                ?.let { thumbs ->
                    if (thumbs.length() > 0) {
                        // Pick the largest thumbnail
                        thumbs.optJSONObject(thumbs.length() - 1)?.optString("url")
                    } else null
                }

            return Track(
                id = "yt_$videoId",
                title = title,
                artist = artist,
                album = album,
                durationMs = durationMs,
                mediaUri = "", // resolved lazily via resolveStreamUrl
                artworkUri = thumbnail,
                isStream = false,
                isYouTube = true,
                youtubeId = videoId
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun parseDurationToMs(text: String): Long {
        if (text.isBlank()) return 0L
        val parts = text.split(":")
        return try {
            when (parts.size) {
                2 -> (parts[0].toLong() * 60 + parts[1].toLong()) * 1000
                3 -> (parts[0].toLong() * 3600 + parts[1].toLong() * 60 + parts[2].toLong()) * 1000
                else -> 0L
            }
        } catch (e: NumberFormatException) {
            0L
        }
    }

    // ── HTTP helpers ─────────────────────────────────────────────────────

    private fun httpPost(urlString: String, body: String): String {
        val conn = (URL(urlString).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            connectTimeout = CONNECT_TIMEOUT
            readTimeout = READ_TIMEOUT
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36")
            setRequestProperty("Referer", "https://music.youtube.com/")
            setRequestProperty("Origin", "https://music.youtube.com")
        }

        OutputStreamWriter(conn.outputStream, Charsets.UTF_8).use { it.write(body) }

        return BufferedReader(InputStreamReader(conn.inputStream, Charsets.UTF_8)).use {
            it.readText()
        }
    }

    private fun httpGet(urlString: String): String {
        val conn = (URL(urlString).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = CONNECT_TIMEOUT
            readTimeout = READ_TIMEOUT
            setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36")
        }

        return BufferedReader(InputStreamReader(conn.inputStream, Charsets.UTF_8)).use {
            it.readText()
        }
    }
}

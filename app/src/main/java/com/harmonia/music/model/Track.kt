package com.harmonia.music.model

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import java.util.Locale
import java.util.concurrent.TimeUnit

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "",
    val durationMs: Long = 0L,
    val mediaUri: String,
    val artworkUri: String? = null,
    val isStream: Boolean = false,
    val streamGenre: String? = null,
    val isFavorite: Boolean = false
) {
    val formattedDuration: String
        get() {
            if (isStream || durationMs <= 0) return "LIVE"
            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60
            return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
        }

    fun toMediaItem(): MediaItem {
        val metadataBuilder = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artist)
            .setAlbumTitle(album)
            .setDisplayTitle(title)

        artworkUri?.let {
            metadataBuilder.setArtworkUri(Uri.parse(it))
        }

        return MediaItem.Builder()
            .setMediaId(id)
            .setUri(mediaUri)
            .setMediaMetadata(metadataBuilder.build())
            .build()
    }

    companion object {
        fun fromMediaItem(mediaItem: MediaItem, durationMs: Long = 0L): Track {
            val metadata = mediaItem.mediaMetadata
            val uriStr = mediaItem.requestMetadata.mediaUri?.toString()
                ?: mediaItem.localConfiguration?.uri?.toString()
                ?: ""
            val isLive = uriStr.startsWith("http://") || uriStr.startsWith("https://")

            return Track(
                id = mediaItem.mediaId,
                title = metadata.title?.toString() ?: "Unknown Title",
                artist = metadata.artist?.toString() ?: "Unknown Artist",
                album = metadata.albumTitle?.toString() ?: "",
                durationMs = durationMs,
                mediaUri = uriStr,
                artworkUri = metadata.artworkUri?.toString(),
                isStream = isLive
            )
        }
    }
}

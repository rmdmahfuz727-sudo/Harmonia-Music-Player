package com.harmonia.music.model

import com.harmonia.music.data.LyricsResult

enum class RepeatMode {
    OFF,
    ALL,
    ONE
}

enum class PlayerStyle(val displayName: String) {
    FLAGSHIP("Mochy Flagship"),
    VINYL("Vinyl Turntable"),
    CANVAS("Minimal Canvas"),
    LYRICS("Lyrics Focus")
}

data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentTrack: Track? = null,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isBuffering: Boolean = false,
    val shuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val queue: List<Track> = emptyList(),
    val queueIndex: Int = -1,
    val errorMessage: String? = null,
    val lyrics: LyricsResult? = null,
    val isLoadingLyrics: Boolean = false,
    val playerStyle: PlayerStyle = PlayerStyle.FLAGSHIP,
    val playbackSpeed: Float = 1.0f,
    val sleepTimerMinutes: Int = 0
) {
    val progressFraction: Float
        get() = if (durationMs > 0) {
            (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
        } else 0f
}

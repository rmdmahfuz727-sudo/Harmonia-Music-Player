package com.harmonia.music.model

data class Playlist(
    val id: String,
    val name: String,
    val trackIds: List<String> = emptyList(),
    val createdAtMs: Long = System.currentTimeMillis(),
    val artworkUri: String? = null,
    val isSmart: Boolean = false
) {
    val trackCount: Int get() = trackIds.size
}

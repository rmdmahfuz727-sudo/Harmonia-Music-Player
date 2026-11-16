package com.harmonia.music.model

data class StreamPreset(
    val name: String,
    val description: String,
    val url: String,
    val genre: String,
    val artworkUrl: String? = null
) {
    fun toTrack(): Track {
        return Track(
            id = "stream_${name.hashCode()}",
            title = name,
            artist = description,
            album = "Live Radio Stream",
            durationMs = 0L,
            mediaUri = url,
            artworkUri = artworkUrl,
            isStream = true,
            streamGenre = genre
        )
    }

    companion object {
        val defaultPresets = listOf(
            StreamPreset(
                name = "Groove Salad",
                description = "Downtempo ambient grooves & chillout",
                url = "https://ice1.somafm.com/groovesalad-128-mp3",
                genre = "Ambient / Chill",
                artworkUrl = "https://somafm.com/img3/groovesalad-400.jpg"
            ),
            StreamPreset(
                name = "DEF CON Radio",
                description = "Music for hacking, coding and late nights",
                url = "https://ice1.somafm.com/defcon-128-mp3",
                genre = "Synthwave / Cyberpunk",
                artworkUrl = "https://somafm.com/img3/defcon-400.jpg"
            ),
            StreamPreset(
                name = "Drone Zone",
                description = "Atmospheric textures with minimal beats",
                url = "https://ice1.somafm.com/dronezone-128-mp3",
                genre = "Deep Space Ambient",
                artworkUrl = "https://somafm.com/img3/dronezone-400.jpg"
            ),
            StreamPreset(
                name = "Secret Agent",
                description = "Spy, lounge, surf, & eclectic vintage sounds",
                url = "https://ice1.somafm.com/secretagent-128-mp3",
                genre = "Lounge / Retro",
                artworkUrl = "https://somafm.com/img3/secretagent-400.jpg"
            ),
            StreamPreset(
                name = "Indie Pop Rocks!",
                description = "New & classic indie rock favorites",
                url = "https://ice1.somafm.com/indiepop-128-mp3",
                genre = "Indie Rock",
                artworkUrl = "https://somafm.com/img3/indiepop-400.jpg"
            ),
            StreamPreset(
                name = "Suburbs of Goa",
                description = "Desi-influenced Asian world and ambient dance",
                url = "https://ice1.somafm.com/suburbsofgoa-128-mp3",
                genre = "World / Goa",
                artworkUrl = "https://somafm.com/img3/suburbsofgoa-400.jpg"
            )
        )
    }
}

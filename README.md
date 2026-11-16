# 🎵 Harmonia — Modern Android Music & Radio Player

[![Author](https://img.shields.io/badge/Developer-rmdmahfuz727--sudo-00E5FF?style=for-the-badge&logo=github)](https://github.com/rmdmahfuz727-sudo)
[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android)](https://www.android.com/)
[![Language](https://img.shields.io/badge/Kotlin-1.9.23-7F52FF?style=for-the-badge&logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%26%20Material%203-4285F4?style=for-the-badge&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Media3](https://img.shields.io/badge/Audio-AndroidX%20Media3%20ExoPlayer-FF6F00?style=for-the-badge)](https://developer.android.com/media/media3)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)

> A modern, sleek native Android hybrid music and live radio streaming player built with **Jetpack Compose**, **Material 3**, and **Android Jetpack Media3 (ExoPlayer)**.

---

## 👨‍💻 Developer & Author

- **Developer:** **[rmdmahfuz727-sudo](https://github.com/rmdmahfuz727-sudo)**
- **GitHub Profile:** [https://github.com/rmdmahfuz727-sudo](https://github.com/rmdmahfuz727-sudo)
- **Project Repository:** [https://github.com/rmdmahfuz727-sudo/Harmonia-Music-Player](https://github.com/rmdmahfuz727-sudo/Harmonia-Music-Player)

---

## 🌟 Highlights

- **Hybrid Audio Engine**: Play offline local audio library tracks (`MP3`, `FLAC`, `AAC`, `OGG`, `WAV`) and real-time live internet radio streams simultaneously.
- **Android Media3 Background Playback**:
  - Implements `HarmoniaPlaybackService` (`MediaSessionService`) for background audio that continues uninterrupted when locked or minimized.
  - System lock-screen controls, notification transport actions, and album art synchronization.
  - Headphone disconnect safety (`handleAudioBecomingNoisy` auto-pauses when headphones/Bluetooth are disconnected).
  - Audio focus management (auto-ducks/pauses on incoming calls and navigation cues).
  - CPU WakeLock (`WAKE_MODE_NETWORK`) prevents stuttering during stream playback when screen turns off.
- **Modern Jetpack Compose UI**:
  - **Obsidian Dark Aesthetic**: Deep obsidian backgrounds accented with vibrant neon cyan, deep purple, and glowing pink.
  - **Docked Mini Player**: Floating above the bottom navigation bar with an animated rotating vinyl disc, marquee song title, play/pause controls, and slim progress bar.
  - **Full-Screen Player Sheet**: Expansive player featuring glowing album art with dynamic shadows, interactive scrub slider, live broadcast badge for streams, shuffle, and cycle repeat modes (`OFF` ➔ `ALL` ➔ `ONE`).
  - **Curated Radio Directory**: Pre-configured high-definition streams (Groove Salad, DEF CON Radio, Drone Zone, Indie Pop Rocks, etc.) + custom stream URL creator.
  - **Smart MediaStore Scanner**: Queries Android MediaStore for local audio files, tags, and album art, automatically filtering out system ringtones and notification sounds.
  - **Favorites Manager**: Star any local song or live stream for 1-tap playback and instant shuffle.
  - **In-App Developer Credits**: Dedicated About modal linking directly to **[@rmdmahfuz727-sudo](https://github.com/rmdmahfuz727-sudo)**.

---

## 📁 Project Architecture

```
HarmoniaPlayer/
├── build.gradle.kts                # Top-level Gradle build configuration
├── settings.gradle.kts             # Module and repository definitions
├── gradle.properties               # JVM args & AndroidX settings
├── gradlew                         # Gradle wrapper shell script
├── LICENSE                         # MIT License credited to rmdmahfuz727-sudo
├── gradle/wrapper/
│   └── gradle-wrapper.properties   # Gradle 8.7 wrapper distribution
└── app/
    ├── build.gradle.kts            # App module dependencies (Media3, Compose BOM, Coil)
    ├── proguard-rules.pro          # ProGuard optimization rules
    └── src/main/
        ├── AndroidManifest.xml     # Permissions & Service declaration
        ├── res/
        │   ├── values/             # strings.xml, colors.xml, themes.xml
        │   ├── drawable/           # Vector icons & default artwork
        │   ├── mipmap-anydpi-v26/  # Adaptive app icons
        │   └── xml/                # Backup & data extraction rules
        └── java/com/harmonia/music/
            ├── HarmoniaApplication.kt       # Application class
            ├── MainActivity.kt              # Entry Activity with edge-to-edge & permission launcher
            ├── model/
            │   ├── Track.kt                 # Track model & Media3 MediaItem mapper
            │   ├── PlaybackState.kt         # Reactive playback state & repeat modes
            │   └── StreamPreset.kt          # Curated internet radio stations
            ├── data/
            │   ├── LocalAudioDataSource.kt  # MediaStore content resolver scanner
            │   ├── OnlineStreamDataSource.kt# Stream presets & custom streams storage
            │   └── MediaRepository.kt       # Unified repository & favorites management
            ├── service/
            │   └── HarmoniaPlaybackService.kt # MediaSessionService hosting ExoPlayer
            └── ui/
                ├── theme/                   # Color, Typography, and Theme definitions
                ├── components/
                │   ├── TrackItem.kt         # Local track row with art & playing wave
                │   ├── StreamItem.kt        # Radio station card with pulsating LIVE tag
                │   ├── MiniPlayer.kt        # Docked bottom bar with rotating disc
                │   └── FullPlayerSheet.kt   # Expandable full-screen playback modal
                ├── screens/
                │   ├── LocalTracksScreen.kt # Local library with shuffle & permissions prompt
                │   ├── OnlineStreamsScreen.kt # Live radio directory & custom stream dialog
                │   ├── FavoritesScreen.kt   # Pinned tracks & streams
                │   └── MainScreen.kt        # Scaffold host & bottom navigation
                └── viewmodel/
                    ├── MusicPlayerController.kt # MediaController bridge to background service
                    └── MainViewModel.kt     # State holder for search, tabs, & lists
```

---

## 🚀 How to Build & Run

### Method 1: In Android Studio (Recommended)
1. Open **Android Studio** (Ladybug, Koala, Iguana, or later).
2. Click **File ➔ Open...** and select the folder:
   `/storage/emulated/0/Documents/HarmoniaPlayer` (or transfer to your PC).
3. Allow Gradle to sync dependencies.
4. Connect an Android device (or launch an Emulator) running **Android 7.0+ (API 24+)**.
5. Click **Run 'app'** (`Shift + F10`).

### Method 2: Command Line (Gradle)
```bash
cd HarmoniaPlayer
./gradlew assembleDebug
```
The output APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🔒 Permissions Used

- `READ_MEDIA_AUDIO`: Android 13+ (API 33+) permission to read local audio files.
- `READ_EXTERNAL_STORAGE`: Android 12 and below fallback for audio library access.
- `POST_NOTIFICATIONS`: Android 13+ permission for system media notification controls.
- `FOREGROUND_SERVICE` & `FOREGROUND_SERVICE_MEDIA_PLAYBACK`: Required on Android 14+ to run playback in the background without termination.
- `INTERNET`: For streaming live audio stations.
- `WAKE_LOCK`: Ensures ExoPlayer streaming does not sleep when screen turns off.

---

## 📜 License

Distributed under the **MIT License**. See `LICENSE` for more information.

Copyright (c) 2026 **[rmdmahfuz727-sudo](https://github.com/rmdmahfuz727-sudo)**

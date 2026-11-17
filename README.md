# 🎵 Harmonia — Mochy-Inspired 120 FPS Android Music & Radio Player with Sakuga Anime VFX

[![Author](https://img.shields.io/badge/Developer-rmdmahfuz727--sudo-E85D75?style=for-the-badge&logo=github)](https://github.com/rmdmahfuz727-sudo)
[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android)](https://www.android.com/)
[![Language](https://img.shields.io/badge/Kotlin-1.9.23-7F52FF?style=for-the-badge&logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%26%20Material%203-4285F4?style=for-the-badge&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Mochy-Music](https://img.shields.io/badge/Design-Mochy--Music%20Aesthetic-E85D75?style=for-the-badge)](https://github.com/realidkroo/Mochy-Music)
[![Sakuga VFX](https://img.shields.io/badge/Physics-Sakuga%20Anime%20VFX-FFD166?style=for-the-badge)](https://sakuga-vfx.vercel.app/gallery/)
[![Media3](https://img.shields.io/badge/Audio-AndroidX%20Media3%20ExoPlayer-FF6F00?style=for-the-badge)](https://developer.android.com/media/media3)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)

> A modern, fluid native Android music and live internet radio player built with **Jetpack Compose**, **Material 3**, and **Android Jetpack Media3 (ExoPlayer)**. Features design language inspired by **Mochy-Music** and interactive anime keyframed spring physics with Canvas-drawn effects inspired by **Sakuga VFX**.

---

## 👨‍💻 Developer & Author

- **Developer:** **[rmdmahfuz727-sudo](https://github.com/rmdmahfuz727-sudo)**
- **GitHub Profile:** [https://github.com/rmdmahfuz727-sudo](https://github.com/rmdmahfuz727-sudo)
- **Project Repository:** [https://github.com/rmdmahfuz727-sudo/Harmonia-Music-Player](https://github.com/rmdmahfuz727-sudo/Harmonia-Music-Player)

---

## 🌸 Mochy-Music Design Aesthetics

Harmonia adopts the signature visual styling of **Mochy-Music**:
- **Ambient Drifting Backdrop**: Dark warm gradient backdrop (`#161519` ➔ `#3D2028` ➔ `#121114`) reflecting the mood of cover art.
- **Mochy Coral & Pill Cluster**: Signature Coral (`#E85D75`) accents, rounded squircle artwork (12–32dp corner radii), and translucent capsule pills (`#992B2830`).
- **Signature Wavy Seek Bar**: Animated harmonic audio wave path along active playback with a glowing white-and-coral nucleus thumb.
- **Action Pills Row**: Horizontal interactive pills for Like / Dislike (100K counter), Music Haptics, Lyrics, and Spatial Audio FX.
- **Capsule Transport Cluster**: Mochy flagship layout featuring Previous Capsule, central Coral Play/Pause FAB, and Next Capsule.
- **Bluetooth Route Display**: Bottom audio output destination indicator (`CPH2745 • SpatialFX`).

---

## 💥 Sakuga Anime VFX & Spring Physics

Inspired by [Sakuga VFX](https://sakuga-vfx.vercel.app/gallery/), controls react like keyframed anime animation cuts:
- **Volume-Preserving Squash & Stretch**: Touching any control (buttons, cards, pills, mini player) squashes along the Y-axis while expanding proportionally along the X-axis (`scaleX * scaleY ≈ 1.0`).
- **Snappy Spring Overshoot**: On release, controls snap past 1.0 on a bouncy spring before settling smoothly into resting state.
- **Canvas-Drawn Anime Effects**:
  - **Impact Starburst (`burst`)**: Sharp triangular anime shards blasting radially on Play/Pause hits.
  - **Shockwave Ring (`ring`)**: Expanding shockwave circle that leaves by thinning out.
  - **Crescent Slash (`slash`)**: Anime sword-blade slash trail on skipping tracks.
  - **Kira Sparkles (`kira`)**: 4-pointed rotating anime glints on favorite hearts.
  - **Manga SFX Typography (`sfx`)**: Bold stylized onomatopoeia popup lettering with ink outlines:
    - **ドン！** (*DON!* — Heavy Impact) on Play FAB trigger.
    - **カチッ！** (*KACHI!* — Mechanical Click) on pill and tab switches.
    - **シュッ！** (*SHU!* — Swift Swish) on track skips and sheet swipes.
    - **ポン！** (*PON!* — Pop) on like toggles.

---

## ⚡ 120 FPS High Refresh Rate Audio Engine

- **RenderThread Hardware Acceleration**: All transforms use `Modifier.graphicsLayer` to execute animations on RenderThread without triggering Compose layout recompositions.
- **Fluid Visualizer**: Canvas-drawn multi-harmonic sine visualizer running at full 120 FPS.
- **Android Media3 Background Playback**:
  - `HarmoniaPlaybackService` (`MediaSessionService`) keeps audio playing when screen is locked or app is minimized.
  - System lock-screen controls, notification transport actions, and album art synchronization.
  - Automatic audio ducking and `AUDIO_BECOMING_NOISY` listener (auto-pauses when headphones/Bluetooth disconnect).
  - CPU WakeLock (`WAKE_MODE_NETWORK`) prevents stuttering during stream playback when screen turns off.

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
            ├── MainActivity.kt              # Entry Activity with 120 FPS display mode
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
                ├── theme/                   # Mochy & Sakuga Color, Typography, and Theme
                ├── components/
                │   ├── SakugaVfx.kt         # Sakuga Anime Physics & Canvas VFX Layer
                │   ├── MochyWaveSeekBar.kt  # Mochy glowing fluid wave seek bar
                │   ├── FluidVisualizer.kt   # 120 FPS Canvas audio visualizer
                │   ├── TrackItem.kt         # Mochy track item with Sakuga squash
                │   ├── StreamItem.kt        # Mochy live station card with pulsing badge
                │   ├── MiniPlayer.kt        # Mochy frosted capsule docked mini bar
                │   └── FullPlayerSheet.kt   # Mochy full-screen player with Sakuga FX
                ├── screens/
                │   ├── LocalTracksScreen.kt # Local library with Mochy action capsules
                │   ├── OnlineStreamsScreen.kt # Live radio directory & custom stream dialog
                │   ├── FavoritesScreen.kt   # Pinned tracks & streams
                │   └── MainScreen.kt        # Scaffold host, Mochy search & bottom navigation
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

## 📜 License

Distributed under the **MIT License**. See `LICENSE` for more information.

Copyright (c) 2026 **[rmdmahfuz727-sudo](https://github.com/rmdmahfuz727-sudo)**

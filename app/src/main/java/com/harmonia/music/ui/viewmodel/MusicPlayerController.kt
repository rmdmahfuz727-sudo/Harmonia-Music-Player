package com.harmonia.music.ui.viewmodel

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.harmonia.music.data.MediaRepository
import com.harmonia.music.model.PlaybackState
import com.harmonia.music.model.RepeatMode
import com.harmonia.music.model.Track
import com.harmonia.music.service.HarmoniaPlaybackService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@UnstableApi
class MusicPlayerController(
    private val context: Context,
    private val scope: CoroutineScope,
    private val repository: MediaRepository
) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var currentQueue: List<Track> = emptyList()
    private var progressTrackerJob: Job? = null

    init {
        initializeController()
    }

    private fun initializeController() {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, HarmoniaPlaybackService::class.java)
        )
        val future = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture = future

        future.addListener({
            try {
                val controller = future.get()
                mediaController = controller
                setupPlayerListener(controller)
                syncStateFromPlayer(controller)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, MoreExecutors.directExecutor())
    }

    private fun setupPlayerListener(player: Player) {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playbackState.update { it.copy(isPlaying = isPlaying) }
                if (isPlaying) {
                    startProgressTracker()
                } else {
                    stopProgressTracker()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                val isBuffering = playbackState == Player.STATE_BUFFERING
                _playbackState.update { it.copy(isBuffering = isBuffering) }
                syncPosition()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                if (mediaItem != null) {
                    val duration = mediaController?.duration ?: 0L
                    val track = currentQueue.find { it.id == mediaItem.mediaId }
                        ?: Track.fromMediaItem(mediaItem, if (duration > 0) duration else 0L)
                    val index = mediaController?.currentMediaItemIndex ?: -1

                    _playbackState.update {
                        it.copy(
                            currentTrack = track,
                            durationMs = if (duration > 0) duration else track.durationMs,
                            queueIndex = index,
                            lyrics = null
                        )
                    }
                    loadLyricsForTrack(track)
                } else {
                    _playbackState.update {
                        it.copy(currentTrack = null, currentPositionMs = 0L, durationMs = 0L, lyrics = null)
                    }
                }
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _playbackState.update { it.copy(shuffleEnabled = shuffleModeEnabled) }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                val mode = when (repeatMode) {
                    Player.REPEAT_MODE_ONE -> RepeatMode.ONE
                    Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                    else -> RepeatMode.OFF
                }
                _playbackState.update { it.copy(repeatMode = mode) }
            }

            override fun onPlayerError(error: PlaybackException) {
                _playbackState.update {
                    it.copy(
                        isPlaying = false,
                        isBuffering = false,
                        errorMessage = error.localizedMessage ?: "Playback error"
                    )
                }
            }
        })
    }

    private fun syncStateFromPlayer(player: Player) {
        val currentItem = player.currentMediaItem
        val track = if (currentItem != null) {
            currentQueue.find { it.id == currentItem.mediaId }
                ?: Track.fromMediaItem(currentItem, player.duration)
        } else null

        val repeat = when (player.repeatMode) {
            Player.REPEAT_MODE_ONE -> RepeatMode.ONE
            Player.REPEAT_MODE_ALL -> RepeatMode.ALL
            else -> RepeatMode.OFF
        }

        _playbackState.update {
            it.copy(
                isPlaying = player.isPlaying,
                currentTrack = track,
                currentPositionMs = player.currentPosition.coerceAtLeast(0L),
                durationMs = if (player.duration > 0) player.duration else (track?.durationMs ?: 0L),
                shuffleEnabled = player.shuffleModeEnabled,
                repeatMode = repeat
            )
        }

        if (player.isPlaying) {
            startProgressTracker()
        }
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressTrackerJob = scope.launch(Dispatchers.Main) {
            while (isActive) {
                syncPosition()
                delay(300)
            }
        }
    }

    private fun stopProgressTracker() {
        progressTrackerJob?.cancel()
        progressTrackerJob = null
    }

    private fun syncPosition() {
        val controller = mediaController ?: return
        val pos = controller.currentPosition.coerceAtLeast(0L)
        val dur = controller.duration
        val trackDur = _playbackState.value.currentTrack?.durationMs ?: 0L
        _playbackState.update {
            it.copy(
                currentPositionMs = pos,
                durationMs = if (dur > 0) dur else trackDur
            )
        }
    }

    fun playQueue(tracks: List<Track>, startIndex: Int = 0) {
        val controller = mediaController ?: return
        if (tracks.isEmpty()) return

        val startTrack = tracks.getOrNull(startIndex) ?: return

        // If the starting track is a YouTube track, resolve its stream URL first
        if (startTrack.isYouTube && startTrack.mediaUri.isBlank() && !startTrack.youtubeId.isNullOrBlank()) {
            _playbackState.update {
                it.copy(
                    isBuffering = true,
                    currentTrack = startTrack,
                    errorMessage = null
                )
            }
            scope.launch {
                val streamUrl = repository.resolveYtStreamUrl(startTrack.youtubeId)
                if (streamUrl != null) {
                    val resolvedTrack = startTrack.copy(mediaUri = streamUrl)
                    val resolvedTracks = tracks.toMutableList()
                    resolvedTracks[startIndex] = resolvedTrack
                    playResolvedQueue(resolvedTracks, startIndex)
                } else {
                    _playbackState.update {
                        it.copy(
                            isBuffering = false,
                            errorMessage = "Could not resolve audio stream"
                        )
                    }
                }
            }
        } else {
            playResolvedQueue(tracks, startIndex)
        }
    }

    private fun playResolvedQueue(tracks: List<Track>, startIndex: Int) {
        val controller = mediaController ?: return
        currentQueue = tracks
        val mediaItems = tracks.map { it.toMediaItem() }

        controller.setMediaItems(mediaItems, startIndex, 0L)
        controller.prepare()
        controller.play()

        _playbackState.update {
            it.copy(
                queue = tracks,
                queueIndex = startIndex,
                currentTrack = tracks.getOrNull(startIndex),
                isPlaying = true,
                errorMessage = null
            )
        }
    }

    fun playSingle(track: Track) {
        playQueue(listOf(track), 0)
    }

    fun togglePlayPause() {
        val controller = mediaController ?: return
        if (controller.isPlaying) {
            controller.pause()
        } else {
            if (controller.playbackState == Player.STATE_IDLE || controller.playbackState == Player.STATE_ENDED) {
                controller.prepare()
            }
            controller.play()
        }
    }

    fun seekTo(positionMs: Long) {
        val controller = mediaController ?: return
        controller.seekTo(positionMs)
        _playbackState.update { it.copy(currentPositionMs = positionMs) }
    }

    fun skipNext() {
        val controller = mediaController ?: return
        if (controller.hasNextMediaItem()) {
            controller.seekToNextMediaItem()
        }
    }

    fun skipPrevious() {
        val controller = mediaController ?: return
        if (controller.currentPosition > 3000) {
            controller.seekTo(0)
        } else if (controller.hasPreviousMediaItem()) {
            controller.seekToPreviousMediaItem()
        } else {
            controller.seekTo(0)
        }
    }

    fun toggleShuffle() {
        val controller = mediaController ?: return
        val newShuffle = !controller.shuffleModeEnabled
        controller.shuffleModeEnabled = newShuffle
        _playbackState.update { it.copy(shuffleEnabled = newShuffle) }
    }

    fun cycleRepeatMode() {
        val controller = mediaController ?: return
        val nextMode = when (controller.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
        controller.repeatMode = nextMode
    }

    fun playQueueAt(index: Int) {
        val controller = mediaController ?: return
        if (index in currentQueue.indices) {
            controller.seekTo(index, 0L)
            controller.play()
        }
    }

    fun loadLyricsForTrack(track: Track) {
        scope.launch {
            _playbackState.update { it.copy(isLoadingLyrics = true) }
            val lyrics = repository.fetchLyrics(track)
            _playbackState.update { it.copy(lyrics = lyrics, isLoadingLyrics = false) }
        }
    }

    fun setPlayerStyle(style: com.harmonia.music.model.PlayerStyle) {
        _playbackState.update { it.copy(playerStyle = style) }
    }

    fun setPlaybackSpeed(speed: Float) {
        mediaController?.setPlaybackSpeed(speed)
        _playbackState.update { it.copy(playbackSpeed = speed) }
    }

    fun setSleepTimer(minutes: Int) {
        _playbackState.update { it.copy(sleepTimerMinutes = minutes) }
        if (minutes > 0) {
            scope.launch {
                delay(minutes * 60 * 1000L)
                mediaController?.pause()
                _playbackState.update { it.copy(sleepTimerMinutes = 0) }
            }
        }
    }

    fun release() {
        stopProgressTracker()
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
    }
}

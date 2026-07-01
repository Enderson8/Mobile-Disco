package com.example.mobiledisco

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MusicPlayer(context: Context) {

    private val player = ExoPlayer.Builder(context).build()

    init {
        player.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }
            }
        )
    }

    private var currentUri: Uri? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    fun play(uri: Uri) {

        currentUri = uri

        val mediaItem = MediaItem.fromUri(uri)

        player.setMediaItem(mediaItem)

        player.prepare()

        player.seekTo(0)

        player.play()

    }

    fun pause() {
        player.pause()
    }

    fun resume() {
        if (player.playbackState == Player.STATE_ENDED) {
            player.seekTo(0)
        }
        player.play()
    }

    fun stop() {

        player.pause()

        player.seekTo(0)

    }

    fun seekTo(position: Long) {
        player.seekTo(position)
    }

    fun checkIsPlaying(): Boolean {
        return player.isPlaying
    }

    fun getCurrentPosition(): Long {
        return player.currentPosition
    }

    fun getDuration(): Long {
        return player.duration
    }

    fun togglePlayback(uri: Uri) {
        if (currentUri == uri) {
            if (checkIsPlaying()) {
                pause()
            } else {
                resume()
            }
        } else {
            play(uri)
        }
    }
}
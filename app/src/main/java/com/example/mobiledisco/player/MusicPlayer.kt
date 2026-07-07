package com.example.mobiledisco.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.media3.common.Player

class MusicPlayer(context: Context) {

    private val player = ExoPlayer.Builder(context).build()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
        })
    }

    fun play(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    fun togglePlayback(uri: Uri) {
        if (player.isPlaying) {
            player.pause()
        } else {
            if (player.mediaItemCount == 0) {
                play(uri)
            } else {
                player.play()
            }
        }
    }

    fun stop() {
        player.pause()
        player.seekTo(0)
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
    }

    fun getCurrentPosition(): Long = player.currentPosition
    fun getDuration(): Long = player.duration

    fun release() {
        player.release()
    }
}

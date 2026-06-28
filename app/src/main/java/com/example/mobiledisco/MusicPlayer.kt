package com.example.mobiledisco

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class MusicPlayer(context: Context) {

    private val player = ExoPlayer.Builder(context).build()

    fun play(uri: Uri) {

        val mediaItem = MediaItem.fromUri(uri)

        player.setMediaItem(mediaItem)

        player.prepare()

        player.play()

    }

    fun pause() {
        player.pause()
    }

    fun stop() {
        player.stop()
    }
}
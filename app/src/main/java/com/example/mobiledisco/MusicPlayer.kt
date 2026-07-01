package com.example.mobiledisco

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class MusicPlayer(context: Context) {

    private val player = ExoPlayer.Builder(context).build()
    private var currentUri: Uri? = null

    fun play(uri: Uri) {

        currentUri = uri

        val mediaItem = MediaItem.fromUri(uri)

        player.setMediaItem(mediaItem)

        player.prepare()

        player.play()

    }

    fun pause() {
        player.pause()
    }

    fun resume() {
        player.play()
    }

    fun stop() {
        player.stop()
    }

    fun isPlaying(): Boolean {
        return player.isPlaying
    }

    fun togglePlayback(uri: Uri) {
        if (currentUri == uri) {
            if (isPlaying()) {
                pause()
            } else {
                resume()
            }
        } else {
            play(uri)
        }
    }
}
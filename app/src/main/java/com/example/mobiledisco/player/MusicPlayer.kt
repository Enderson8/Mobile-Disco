package com.example.mobiledisco.player

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.mobiledisco.data.Song
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MusicPlayer(context: Context) {

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private val controller: MediaController?
        get() = if (controllerFuture?.isDone == true) controllerFuture?.get() else null

    private val _isPlaying = MutableStateFlow(value = false)
    val isPlaying = _isPlaying.asStateFlow()

    var onSongFinished: (() -> Unit)? = null
    var onPlayerReady: (() -> Unit)? = null
    var onMediaItemTransition: ((String) -> Unit)? = null

    init {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        
        controllerFuture?.addListener({
            val controller = controllerFuture?.get()
            if (controller != null) {
                _isPlaying.value = controller.isPlaying
                controller.addListener(
                    object : Player.Listener {
                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            _isPlaying.value = isPlaying
                        }

                        override fun onPlaybackStateChanged(playbackState: Int) {
                            if (playbackState == Player.STATE_ENDED) {
                                Log.d("MobileDisco", "STATE_ENDED detectado")
                                onSongFinished?.invoke()
                            }
                        }

                        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                            mediaItem?.mediaId?.let { uri ->
                                onMediaItemTransition?.invoke(uri)
                            }
                        }
                    }
                )
                
                onPlayerReady?.invoke()
            }
        }, MoreExecutors.directExecutor())
    }

    fun play(song: Song, position: Long = 0L) {
        val player = controller ?: return
        
        // Se a música já é a atual, apenas dá play
        if (player.currentMediaItem?.mediaId == song.uri) {
            if (position > 0) player.seekTo(position)
            player.play()
            return
        }

        // Caso contrário, tenta encontrar na fila atual
        for (i in 0 until player.mediaItemCount) {
            if (player.getMediaItemAt(i).mediaId == song.uri) {
                player.seekTo(i, position)
                player.play()
                return
            }
        }

        // Se não estiver na fila, carrega como item único (fallback)
        val mediaItem = createMediaItem(song)
        player.setMediaItem(mediaItem)
        if (position > 0) player.seekTo(position)
        player.prepare()
        player.play()
    }

    fun updateQueue(songs: List<Song>, currentIndex: Int) {
        val player = controller ?: return
        
        val mediaItems = songs.map { createMediaItem(it) }

        player.setMediaItems(mediaItems, currentIndex, C.TIME_UNSET)
        player.prepare()
    }

    private fun createMediaItem(song: Song): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(song.name)
            .setArtist(song.artist)
            .setAlbumTitle(song.album)
            .setArtworkData(song.cover, MediaMetadata.PICTURE_TYPE_FRONT_COVER)
            .build()

        return MediaItem.Builder()
            .setUri(song.uri.toUri())
            .setMediaId(song.uri)
            .setMediaMetadata(metadata)
            .build()
    }

    fun prepare(song: Song, position: Long = 0L) {
        val player = controller ?: return
        
        // Verifica se já está na fila
        for (i in 0 until player.mediaItemCount) {
            if (player.getMediaItemAt(i).mediaId == song.uri) {
                player.seekTo(i, position)
                player.pause()
                return
            }
        }

        val mediaItem = createMediaItem(song)
        player.setMediaItem(mediaItem)
        player.seekTo(position)
        player.prepare()
        player.pause()
    }

    fun togglePlayback() {
        val player = controller ?: return
        if (player.isPlaying) {
            player.pause()
        } else {
            if (player.mediaItemCount > 0) {
                player.play()
            }
        }
    }

    fun stop() {
        val player = controller ?: return
        player.pause()
        player.seekTo(0)
    }

    fun seekTo(position: Long) {
        controller?.seekTo(position)
    }

    fun next() {
        controller?.seekToNext()
    }

    fun previous() {
        controller?.seekToPrevious()
    }

    fun getCurrentPosition(): Long = controller?.currentPosition ?: 0L
    fun getDuration(): Long = controller?.duration ?: 0L

    fun release() {
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
    }
}

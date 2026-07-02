package com.example.mobiledisco

import android.app.Application
import android.net.Uri
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MusicViewModel(
    application: Application
) : AndroidViewModel(application) {

    val player = MusicPlayer(application)

    private val prefs = application.getSharedPreferences(
        "mobile_disco",
        Context.MODE_PRIVATE
    )

    private val _biblioteca = MutableStateFlow(listOf<Song>())
    val biblioteca = _biblioteca.asStateFlow()

    private val _musicaSelecionada = MutableStateFlow<Song?>(null)
    val musicaSelecionada = _musicaSelecionada.asStateFlow()

    val isPlaying = player.isPlaying

    private var musicaAtualIndex = 0

    init {
        carregarBiblioteca()
    }

    private fun carregarBiblioteca() {
        val musicasSalvas = prefs.all
        _biblioteca.value = musicasSalvas.map { item ->
            Song(
                name = item.key,
                uri = item.value.toString(),
                id = System.currentTimeMillis()
            )
        }
    }

    fun selecionarMusica(song: Song?) {
        _musicaSelecionada.value = song
        if (song != null) {
            musicaAtualIndex = _biblioteca.value.indexOf(song)
            player.play(Uri.parse(song.uri))
        }
    }

    fun adicionarMusica(song: Song) {
        _biblioteca.value = _biblioteca.value + song

        prefs.edit()
            .putString(
                song.name,
                song.uri
            )
            .apply()
    }

    fun adicionarMusicas(songs: List<Song>) {
        _biblioteca.value = _biblioteca.value + songs
    }

    fun limparBiblioteca() {
        _biblioteca.value = emptyList()
        _musicaSelecionada.value = null
        musicaAtualIndex = 0
        prefs.edit()
            .clear()
            .apply()
        player.stop()
    }

    fun toggle(uri: Uri) {
        player.togglePlayback(uri)
    }

    fun stop() {
        player.stop()
    }

    fun proximaMusica() {
        if (musicaAtualIndex < _biblioteca.value.size - 1) {
            musicaAtualIndex++
            val proxima = _biblioteca.value[musicaAtualIndex]
            _musicaSelecionada.value = proxima
            player.play(Uri.parse(proxima.uri))
        }
    }

    fun anteriorMusica() {
        if (musicaAtualIndex > 0) {
            musicaAtualIndex--
            val anterior = _biblioteca.value[musicaAtualIndex]
            _musicaSelecionada.value = anterior
            player.play(Uri.parse(anterior.uri))
        }
    }

    override fun onCleared() {
        player.release()
        super.onCleared()
    }
}

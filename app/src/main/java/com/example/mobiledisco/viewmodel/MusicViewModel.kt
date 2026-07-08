package com.example.mobiledisco.viewmodel

import android.app.Application
import android.net.Uri
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobiledisco.data.MusicMetadata
import com.example.mobiledisco.data.Song
import com.example.mobiledisco.player.MusicPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicViewModel(
    application: Application
) : AndroidViewModel(application) {

    val player = MusicPlayer(application)

    private val prefs = application.getSharedPreferences(
        "mobile_disco_v3", // Atualizando para v3 devido à adição do trackNumber
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
        atualizarMetadadosEmSegundoPlano(application)
    }

    private fun carregarBiblioteca() {
        val musicasSalvas = prefs.all
        _biblioteca.value = musicasSalvas.mapNotNull { item ->
            try {
                val data = item.value.toString().split(":::")
                if (data.size >= 5) {
                    Song(
                        uri = data[0],
                        name = data[1],
                        artist = data[2],
                        album = data[3],
                        duration = data[4].toLongOrNull() ?: 0L,
                        id = data[0].hashCode().toLong(),
                        cover = null,
                        trackNumber = data.getOrNull(5)?.toIntOrNull() ?: 0
                    )
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun atualizarMetadadosEmSegundoPlano(context: Context) {
        viewModelScope.launch {
            val musicasAtualizadas = mutableListOf<Song>()
            withContext(Dispatchers.IO) {
                _biblioteca.value.forEach { song ->
                    try {
                        val uri = Uri.parse(song.uri)
                        val metadata = MusicMetadata.read(context, uri)
                        // Criamos um novo objeto Song com a capa carregada e trackNumber
                        val songCompleto = Song(
                            uri = song.uri,
                            name = metadata.title,
                            artist = metadata.artist,
                            album = metadata.album,
                            duration = metadata.duration,
                            id = song.id,
                            cover = metadata.cover,
                            trackNumber = metadata.trackNumber
                        )
                        musicasAtualizadas.add(songCompleto)
                    } catch (e: Exception) {
                        musicasAtualizadas.add(song) // Mantém o original se der erro
                    }
                }
            }
            _biblioteca.value = musicasAtualizadas
        }
    }

    fun selecionarMusica(song: Song?) {
        _musicaSelecionada.value = song
        if (song != null) {
            musicaAtualIndex = _biblioteca.value.indexOfFirst { it.uri == song.uri }
            player.play(Uri.parse(song.uri))
        }
    }

    fun adicionarMusica(song: Song) {
        if (_biblioteca.value.any { it.uri == song.uri }) return

        _biblioteca.value = _biblioteca.value + song
        persistirMusica(song)
    }

    fun adicionarMusicas(songs: List<Song>) {
        val novasMusicas = songs.filter { nova -> 
            _biblioteca.value.none { it.uri == nova.uri } 
        }
        
        if (novasMusicas.isEmpty()) return

        _biblioteca.value = _biblioteca.value + novasMusicas
        
        novasMusicas.forEach { persistirMusica(it) }
    }

    private fun persistirMusica(song: Song) {
        val metadataString = "${song.uri}:::${song.name}:::${song.artist}:::${song.album}:::${song.duration}:::${song.trackNumber}"
        prefs.edit()
            .putString(song.uri, metadataString)
            .apply()
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

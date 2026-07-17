package com.example.mobiledisco.viewmodel

import android.app.Application
import android.net.Uri
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobiledisco.data.MusicMetadata
import com.example.mobiledisco.data.Playlist
import com.example.mobiledisco.data.PlaylistRepository
import com.example.mobiledisco.data.Song
import com.example.mobiledisco.player.MusicPlayer
import com.example.mobiledisco.player.PlayerEvent
import com.example.mobiledisco.player.RepeatMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicViewModel(
    application: Application
) : AndroidViewModel(application) {

    val player = MusicPlayer(application)
    private val playlistRepository = PlaylistRepository(application)

    private val prefs = application.getSharedPreferences(
        "mobile_disco_v3",
        Context.MODE_PRIVATE
    )

    private val _biblioteca = MutableStateFlow(listOf<Song>())
    val biblioteca = _biblioteca.asStateFlow()

    private val _musicaSelecionada = MutableStateFlow<Song?>(null)
    val musicaSelecionada = _musicaSelecionada.asStateFlow()

    private val _filaReproducao = MutableStateFlow(listOf<Song>())
    val filaReproducao = _filaReproducao.asStateFlow()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists = _playlists.asStateFlow()

    private val _favoritos = MutableStateFlow<Set<String>>(emptySet())
    val favoritos = _favoritos.asStateFlow()

    private val _isEditingPlaylist = MutableStateFlow<Long?>(null)
    val isEditingPlaylist = _isEditingPlaylist.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled = _isShuffleEnabled.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.NONE)
    val repeatMode = _repeatMode.asStateFlow()

    val isPlaying = player.isPlaying

    private var indexNaFila = 0
    private var filaOriginal = listOf<Song>()

    init {
        carregarConfiguracoes()
        carregarBiblioteca()
        carregarPlaylists()
        carregarFavoritos()
        atualizarMetadadosEmSegundoPlano(application)
        
        player.onSongFinished = {
            handleAutoNext()
        }

        player.onMediaItemTransition = { uri ->
            Log.d("MobileDisco", "Transição de mídia detectada: $uri")
            val musica = _filaReproducao.value.find { it.uri == uri }
            if (musica != null && _musicaSelecionada.value?.uri != uri) {
                _musicaSelecionada.value = musica
                indexNaFila = _filaReproducao.value.indexOf(musica).coerceAtLeast(0)
                prefs.edit()
                    .putString("SESSION_LAST_URI", uri)
                    .putLong("SESSION_LAST_POS", 0L)
                    .apply()
            }
        }

        player.onPlayerReady = {
            restaurarUltimaSessao()
        }

        viewModelScope.launch {
            while (true) {
                if (player.isPlaying.value) {
                    val pos = player.getCurrentPosition()
                    _currentPosition.value = pos
                    _duration.value = player.getDuration()
                    
                    _musicaSelecionada.value?.let { 
                        prefs.edit()
                            .putLong("SESSION_LAST_POS", pos)
                            .putBoolean("SESSION_WAS_PLAYING", true)
                            .apply()
                    }
                } else if (_musicaSelecionada.value != null) {
                    prefs.edit().putBoolean("SESSION_WAS_PLAYING", false).apply()
                }
                delay(1000)
            }
        }
    }

    private fun carregarConfiguracoes() {
        _isShuffleEnabled.value = prefs.getBoolean("SETTINGS_SHUFFLE", false)
        val repeatOrdinal = prefs.getInt("SETTINGS_REPEAT", RepeatMode.NONE.ordinal)
        _repeatMode.value = RepeatMode.values()[repeatOrdinal]
    }

    private fun carregarFavoritos() {
        _favoritos.value = prefs.getStringSet("SETTINGS_FAVORITOS", emptySet()) ?: emptySet()
    }

    private fun restaurarUltimaSessao() {
        val lastUri = prefs.getString("SESSION_LAST_URI", null)
        val lastPos = prefs.getLong("SESSION_LAST_POS", 0L)
        val wasPlaying = prefs.getBoolean("SESSION_WAS_PLAYING", false)

        if (lastUri != null) {
            val song = _biblioteca.value.find { it.uri == lastUri }
            if (song != null) {
                _musicaSelecionada.value = song
                _currentPosition.value = lastPos
                
                configurarFila(song)

                if (wasPlaying) {
                    player.play(song, lastPos)
                } else {
                    player.prepare(song, lastPos)
                }
            }
        }
    }

    private fun carregarBiblioteca() {
        val musicasSalvas = prefs.all
        _biblioteca.value = musicasSalvas.mapNotNull { item ->
            try {
                if (item.key.startsWith("SESSION_") || item.key.startsWith("SETTINGS_")) return@mapNotNull null
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
                        
                        if (_musicaSelecionada.value?.uri == songCompleto.uri) {
                            _musicaSelecionada.value = songCompleto
                        }
                    } catch (e: Exception) {
                        musicasAtualizadas.add(song)
                    }
                }
            }
            _biblioteca.value = musicasAtualizadas
        }
    }

    fun selecionarMusica(song: Song?) {
        _musicaSelecionada.value = song
        if (song != null) {
            prefs.edit()
                .putString("SESSION_LAST_URI", song.uri)
                .putBoolean("SESSION_WAS_PLAYING", true)
                .apply()

            configurarFila(song)
            player.updateQueue(_filaReproducao.value, indexNaFila)
            player.play(song)
        } else {
            _filaReproducao.value = emptyList()
            indexNaFila = 0
            prefs.edit().remove("SESSION_LAST_URI").remove("SESSION_LAST_POS").remove("SESSION_WAS_PLAYING").apply()
        }
    }

    private fun configurarFila(song: Song) {
        filaOriginal = _biblioteca.value
            .filter { it.album == song.album && it.artist == song.artist }
            .sortedBy { it.trackNumber }
        
        atualizarFilaVisual(song)
    }

    private fun atualizarFilaVisual(songAtual: Song?) {
        val novaFila = if (_isShuffleEnabled.value) {
            val shuffled = filaOriginal.shuffled().toMutableList()
            songAtual?.let { current ->
                shuffled.remove(current)
                shuffled.add(0, current)
            }
            shuffled
        } else {
            filaOriginal
        }
        
        _filaReproducao.value = novaFila
        indexNaFila = if (songAtual != null) {
            novaFila.indexOfFirst { it.uri == songAtual.uri }.coerceAtLeast(0)
        } else 0
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
        _filaReproducao.value = emptyList()
        _currentPosition.value = 0L
        _duration.value = 0L
        indexNaFila = 0
        prefs.edit()
            .clear()
            .apply()
        player.stop()
    }

    fun toggle() {
        player.togglePlayback()
    }

    fun stop() {
        player.stop()
        _currentPosition.value = 0L
        prefs.edit()
            .putLong("SESSION_LAST_POS", 0L)
            .putBoolean("SESSION_WAS_PLAYING", false)
            .apply()
    }

    fun handlePlayerEvent(event: PlayerEvent) {
        when (event) {
            PlayerEvent.PlayPause -> _musicaSelecionada.value?.let { toggle() }
            PlayerEvent.Stop -> stop()
            PlayerEvent.Next -> player.next()
            PlayerEvent.Previous -> player.previous()
            is PlayerEvent.Seek -> {
                player.seekTo(event.position)
                _currentPosition.value = event.position
            }
            PlayerEvent.ToggleShuffle -> toggleShuffle()
            PlayerEvent.ToggleRepeat -> toggleRepeat()
            is PlayerEvent.ToggleFavorite -> toggleFavorito(event.song)
        }
    }

    private fun toggleShuffle() {
        val newValue = !_isShuffleEnabled.value
        _isShuffleEnabled.value = newValue
        prefs.edit().putBoolean("SETTINGS_SHUFFLE", newValue).apply()
        atualizarFilaVisual(_musicaSelecionada.value)
    }

    private fun toggleRepeat() {
        val modes = RepeatMode.values()
        val nextMode = modes[(_repeatMode.value.ordinal + 1) % modes.size]
        _repeatMode.value = nextMode
        prefs.edit().putInt("SETTINGS_REPEAT", nextMode.ordinal).apply()
    }

    private fun toggleFavorito(song: Song) {
        val current = _favoritos.value.toMutableSet()
        if (current.contains(song.uri)) {
            current.remove(song.uri)
        } else {
            current.add(song.uri)
        }
        _favoritos.value = current
        prefs.edit().putStringSet("SETTINGS_FAVORITOS", current).apply()
    }

    private fun handleAutoNext() {
        if (_repeatMode.value == RepeatMode.ONE) {
            _musicaSelecionada.value?.let { player.play(it) }
        } else {
            proximaMusica()
        }
    }

    fun proximaMusica() {
        val fila = _filaReproducao.value
        if (fila.isNotEmpty()) {
            if (indexNaFila < fila.size - 1) {
                indexNaFila++
                tocarMusicaDaFila(fila[indexNaFila])
            } else if (_repeatMode.value == RepeatMode.ALL) {
                indexNaFila = 0
                tocarMusicaDaFila(fila[indexNaFila])
            }
        }
    }

    fun anteriorMusica() {
        val fila = _filaReproducao.value
        if (fila.isNotEmpty()) {
            if (indexNaFila > 0) {
                indexNaFila--
                tocarMusicaDaFila(fila[indexNaFila])
            } else if (_repeatMode.value == RepeatMode.ALL) {
                indexNaFila = fila.size - 1
                tocarMusicaDaFila(fila[indexNaFila])
            }
        }
    }

    private fun tocarMusicaDaFila(song: Song) {
        _musicaSelecionada.value = song
        prefs.edit()
            .putString("SESSION_LAST_URI", song.uri)
            .putLong("SESSION_LAST_POS", 0L)
            .putBoolean("SESSION_WAS_PLAYING", true)
            .apply()
        player.play(song)
    }

    // --- Lógica de Playlists ---

    fun iniciarEdicaoPlaylist(playlistId: Long) {
        _isEditingPlaylist.value = playlistId
    }

    fun concluirEdicaoPlaylist() {
        _isEditingPlaylist.value = null
    }

    fun selecionarMusicaDaPlaylist(playlist: Playlist, song: Song) {
        _musicaSelecionada.value = song
        prefs.edit()
            .putString("SESSION_LAST_URI", song.uri)
            .putBoolean("SESSION_WAS_PLAYING", true)
            .apply()
        filaOriginal = playlist.songs
        atualizarFilaVisual(song)
        player.updateQueue(_filaReproducao.value, indexNaFila)
        player.play(song)
    }

    private fun carregarPlaylists() {
        _playlists.value = playlistRepository.loadPlaylists(_biblioteca.value)
    }

    fun criarPlaylist(nome: String) {
        val novaPlaylist = Playlist(id = System.currentTimeMillis(), name = nome)
        val novasPlaylists = (_playlists.value + novaPlaylist).sortedBy { it.name }
        _playlists.value = novasPlaylists
        playlistRepository.savePlaylists(novasPlaylists)
    }

    fun removerPlaylist(playlistId: Long) {
        val novasPlaylists = _playlists.value.filter { it.id != playlistId }
        _playlists.value = novasPlaylists
        playlistRepository.savePlaylists(novasPlaylists)
    }

    fun renomearPlaylist(playlistId: Long, novoNome: String) {
        val novasPlaylists = _playlists.value.map { playlist ->
            if (playlist.id == playlistId) playlist.copy(name = novoNome) else playlist
        }
        _playlists.value = novasPlaylists
        playlistRepository.savePlaylists(novasPlaylists)
    }

    fun adicionarMusicaNaPlaylist(playlistId: Long, song: Song): Boolean {
        var added = false
        val novasPlaylists = _playlists.value.map { playlist ->
            if (playlist.id == playlistId) {
                if (!playlist.songs.any { it.uri == song.uri }) {
                    added = true
                    playlist.copy(songs = playlist.songs + song)
                } else playlist
            } else playlist
        }
        if (added) {
            _playlists.value = novasPlaylists
            playlistRepository.savePlaylists(novasPlaylists)
        }
        return added
    }

    fun removerMusicaDaPlaylist(playlistId: Long, song: Song) {
        val novasPlaylists = _playlists.value.map { playlist ->
            if (playlist.id == playlistId) {
                playlist.copy(songs = playlist.songs.filter { it.uri != song.uri })
            } else playlist
        }
        _playlists.value = novasPlaylists
        playlistRepository.savePlaylists(novasPlaylists)
    }

    override fun onCleared() {
        player.release()
        super.onCleared()
    }
}

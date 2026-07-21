package com.example.mobiledisco.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.core.content.edit
import androidx.core.net.toUri
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
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.example.mobiledisco.ui.state.FilterOption
import com.example.mobiledisco.ui.state.SortDirection
import com.example.mobiledisco.ui.state.SortField
import com.example.mobiledisco.ui.state.SortOrder
import com.example.mobiledisco.data.toAlbums
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.json.JSONArray
import org.json.JSONObject

data class MusicStatistics(
    val totalSongs: Int = 0,
    val totalAlbums: Int = 0,
    val totalPlaylists: Int = 0,
    val totalFavorites: Int = 0,
    val totalHistory: Int = 0,
    val totalPlays: Int = 0,
    val mostPlayedSong: Pair<Song, Int>? = null,
    val mostPlayedArtist: Pair<String, Int>? = null,
    val mostPlayedAlbum: Pair<String, Int>? = null
)

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

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists = _playlists.asStateFlow()

    private val _favoritos = MutableStateFlow<Set<String>>(emptySet())
    val favoritos = _favoritos.asStateFlow()

    private val _historico = MutableStateFlow<List<String>>(emptyList())
    val historico = _historico.asStateFlow()

    private val _playCounts = MutableStateFlow<Map<String, Int>>(emptyMap())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder())
    val sortOrder = _sortOrder.asStateFlow()

    private val _filterOption = MutableStateFlow(FilterOption.ALL)
    val filterOption = _filterOption.asStateFlow()

    private fun matchesSearch(song: Song, query: String): Boolean {
        if (query.isBlank()) return true
        return song.name.contains(query, ignoreCase = true) ||
               song.artist.contains(query, ignoreCase = true) ||
               song.album.contains(query, ignoreCase = true)
    }

    val filteredPlaylists = combine(playlists, searchQuery) { plists, query ->
        if (query.isBlank()) plists
        else plists.filter { it.name.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val filteredBiblioteca = combine(biblioteca, searchQuery, sortOrder, _playCounts) { songs, query, sort, counts ->
        val filtered = songs.filter { matchesSearch(it, query) }

        when (sort.field) {
            SortField.NAME -> if (sort.direction == SortDirection.ASCENDING) filtered.sortedBy { it.name.lowercase() } else filtered.sortedByDescending { it.name.lowercase() }
            SortField.ARTIST -> if (sort.direction == SortDirection.ASCENDING) filtered.sortedBy { it.artist.lowercase() } else filtered.sortedByDescending { it.artist.lowercase() }
            SortField.ALBUM -> if (sort.direction == SortDirection.ASCENDING) filtered.sortedBy { it.album.lowercase() } else filtered.sortedByDescending { it.album.lowercase() }
            SortField.IMPORT_DATE -> if (sort.direction == SortDirection.ASCENDING) filtered.sortedBy { it.importDate } else filtered.sortedByDescending { it.importDate }
            SortField.MOST_PLAYED -> {
                val sorted = filtered.sortedByDescending { counts[it.uri] ?: 0 }
                if (sort.direction == SortDirection.ASCENDING) sorted.reversed() else sorted
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val filteredHistorico = combine(biblioteca, historico, searchQuery) { songs, histUris, query ->
        histUris.asSequence()
            .mapNotNull { uri -> songs.find { it.uri == uri } }
            .filter { matchesSearch(it, query) }
            .toList()
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val filteredMaisTocadas = combine(biblioteca, _playCounts, searchQuery) { songs, counts, query ->
        counts.asSequence()
            .filter { it.value > 0 }
            .mapNotNull { entry -> songs.find { it.uri == entry.key }?.let { it to entry.value } }
            .filter { (song, _) -> matchesSearch(song, query) }
            .sortedByDescending { it.second }
            .take(20)
            .toList()
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val statistics = combine(
        biblioteca,
        playlists,
        favoritos,
        historico,
        _playCounts
    ) { songs, playlists, favs, hist, counts ->
        val albums = songs.toAlbums()
        val totalPlays = counts.values.sum()
        
        val mostPlayedSong = counts.maxByOrNull { it.value }?.let { entry ->
            songs.find { it.uri == entry.key }?.let { it to entry.value }
        }

        val artistCounts = counts.asSequence()
            .mapNotNull { entry ->
                songs.find { it.uri == entry.key }?.let { it.artist to entry.value }
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { it.value.sum() }
        
        val mostPlayedArtist = artistCounts.maxByOrNull { it.value }?.toPair()

        val albumCounts = counts.asSequence()
            .mapNotNull { entry ->
                songs.find { it.uri == entry.key }?.let { "${it.album} - ${it.artist}" to entry.value }
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { it.value.sum() }

        val mostPlayedAlbum = albumCounts.maxByOrNull { it.value }?.toPair()

        MusicStatistics(
            totalSongs = songs.size,
            totalAlbums = albums.size,
            totalPlaylists = playlists.size,
            totalFavorites = favs.size,
            totalHistory = hist.size,
            totalPlays = totalPlays,
            mostPlayedSong = mostPlayedSong,
            mostPlayedArtist = mostPlayedArtist,
            mostPlayedAlbum = mostPlayedAlbum
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, MusicStatistics())

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
        carregarHistorico()
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
                prefs.edit {
                    putString("SESSION_LAST_URI", uri)
                    putLong("SESSION_LAST_POS", 0L)
                }
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
                        prefs.edit {
                            putLong("SESSION_LAST_POS", pos)
                            putBoolean("SESSION_WAS_PLAYING", true)
                        }
                    }
                } else if (_musicaSelecionada.value != null) {
                    prefs.edit { putBoolean("SESSION_WAS_PLAYING", false) }
                }
                delay(1.seconds)
            }
        }
    }

    private fun carregarConfiguracoes() {
        _isShuffleEnabled.value = prefs.getBoolean("SETTINGS_SHUFFLE", false)
        val repeatOrdinal = prefs.getInt("SETTINGS_REPEAT", RepeatMode.NONE.ordinal)
        _repeatMode.value = RepeatMode.entries.getOrElse(repeatOrdinal) { RepeatMode.NONE }

        val sortField = SortField.entries.find { it.name == prefs.getString("SETTINGS_SORT_FIELD", null) } ?: SortField.NAME
        val sortDirection = SortDirection.entries.find { it.name == prefs.getString("SETTINGS_SORT_DIRECTION", null) } ?: SortDirection.ASCENDING
        _sortOrder.value = SortOrder(sortField, sortDirection)

        val filterName = prefs.getString("SETTINGS_FILTER_OPTION", FilterOption.ALL.name) ?: FilterOption.ALL.name
        _filterOption.value = FilterOption.entries.find { it.name == filterName } ?: FilterOption.ALL

        carregarEstatisticas()
    }

    private fun carregarEstatisticas() {
        val counts = mutableMapOf<String, Int>()
        prefs.all.forEach { (key, value) ->
            if (key.startsWith("STATS_PLAY_COUNT_")) {
                val uri = key.removePrefix("STATS_PLAY_COUNT_")
                val count = value as? Int ?: 0
                counts[uri] = count
            }
        }
        _playCounts.value = counts
    }

    private fun incrementarReproducao(song: Song) {
        val currentCounts = _playCounts.value.toMutableMap()
        val newCount = (currentCounts[song.uri] ?: 0) + 1
        currentCounts[song.uri] = newCount
        _playCounts.value = currentCounts
        prefs.edit { putInt("STATS_PLAY_COUNT_${song.uri}", newCount) }
    }

    private fun carregarFavoritos() {
        _favoritos.value = prefs.getStringSet("SETTINGS_FAVORITOS", emptySet()) ?: emptySet()
    }

    private fun carregarHistorico() {
        val historyString = prefs.getString("SETTINGS_HISTORICO", "") ?: ""
        if (historyString.isNotEmpty()) {
            _historico.value = historyString.split(";;;").filter { it.isNotEmpty() }
        }
    }

    private fun adicionarAoHistorico(song: Song) {
        val current = _historico.value.toMutableList()
        // Se já for o primeiro (mais recente), não faz nada
        if (current.firstOrNull() == song.uri) return
        
        // Remove se já existir em outra posição para trazer para o topo
        current.remove(song.uri)
        current.add(0, song.uri)
        
        // Limita a 50
        val limited = current.take(50)
        _historico.value = limited
        
        prefs.edit().putString("SETTINGS_HISTORICO", limited.joinToString(";;;")).apply()
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
                if (item.key.startsWith("SESSION_") || item.key.startsWith("SETTINGS_") || item.key.startsWith("STATS_")) return@mapNotNull null
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
                        trackNumber = data.getOrNull(5)?.toIntOrNull() ?: 0,
                        importDate = data.getOrNull(6)?.toLongOrNull() ?: System.currentTimeMillis()
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
                        val uri = song.uri.toUri()
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
                    } catch (_: Exception) {
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
            prefs.edit {
                putString("SESSION_LAST_URI", song.uri)
                putBoolean("SESSION_WAS_PLAYING", true)
            }

            configurarFila(song)
            player.updateQueue(_filaReproducao.value, indexNaFila)
            player.play(song)
            adicionarAoHistorico(song)
            incrementarReproducao(song)
        } else {
            _filaReproducao.value = emptyList()
            indexNaFila = 0
            prefs.edit {
                remove("SESSION_LAST_URI")
                remove("SESSION_LAST_POS")
                remove("SESSION_WAS_PLAYING")
            }
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
        val metadataString = "${song.uri}:::${song.name}:::${song.artist}:::${song.album}:::${song.duration}:::${song.trackNumber}:::${song.importDate}"
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
        prefs.edit { clear() }
        player.stop()
    }

    fun limparHistorico() {
        _historico.value = emptyList()
        prefs.edit { remove("SETTINGS_HISTORICO") }
    }

    fun limparFavoritos() {
        _favoritos.value = emptySet()
        prefs.edit { remove("SETTINGS_FAVORITOS") }
    }

    fun zerarEstatisticas() {
        _playCounts.value = emptyMap()
        val editor = prefs.edit()
        prefs.all.keys.forEach { key ->
            if (key.startsWith("STATS_PLAY_COUNT_")) {
                editor.remove(key)
            }
        }
        editor.apply()
    }

    fun toggle() {
        player.togglePlayback()
    }

    fun stop() {
        player.stop()
        _currentPosition.value = 0L
        prefs.edit {
            putLong("SESSION_LAST_POS", 0L)
            putBoolean("SESSION_WAS_PLAYING", false)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSortOrder(order: SortOrder) {
        _sortOrder.value = order
        prefs.edit {
            putString("SETTINGS_SORT_FIELD", order.field.name)
            putString("SETTINGS_SORT_DIRECTION", order.direction.name)
        }
    }

    fun updateFilterOption(option: FilterOption) {
        _filterOption.value = option
        prefs.edit { putString("SETTINGS_FILTER_OPTION", option.name) }
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
        prefs.edit { putBoolean("SETTINGS_SHUFFLE", newValue) }
        atualizarFilaVisual(_musicaSelecionada.value)
    }

    private fun toggleRepeat() {
        val modes = RepeatMode.entries
        val nextMode = modes[(_repeatMode.value.ordinal + 1) % modes.size]
        _repeatMode.value = nextMode
        prefs.edit { putInt("SETTINGS_REPEAT", nextMode.ordinal) }
    }

    private fun toggleFavorito(song: Song) {
        val current = _favoritos.value.toMutableSet()
        if (current.contains(song.uri)) {
            current.remove(song.uri)
        } else {
            current.add(song.uri)
        }
        _favoritos.value = current
        prefs.edit { putStringSet("SETTINGS_FAVORITOS", current) }
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

    private fun tocarMusicaDaFila(song: Song) {
        _musicaSelecionada.value = song
        prefs.edit {
            putString("SESSION_LAST_URI", song.uri)
            putLong("SESSION_LAST_POS", 0L)
            putBoolean("SESSION_WAS_PLAYING", true)
        }
        player.play(song)
        adicionarAoHistorico(song)
        incrementarReproducao(song)
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
        prefs.edit {
            putString("SESSION_LAST_URI", song.uri)
            putBoolean("SESSION_WAS_PLAYING", true)
        }
        filaOriginal = playlist.songs
        atualizarFilaVisual(song)
        player.updateQueue(_filaReproducao.value, indexNaFila)
        player.play(song)
        adicionarAoHistorico(song)
        incrementarReproducao(song)
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

    fun exportarDados(): String {
        try {
            val root = JSONObject()
            root.put("version", 1)

            // Biblioteca
            val libArray = JSONArray()
            _biblioteca.value.forEach { song ->
                val s = JSONObject()
                s.put("uri", song.uri)
                s.put("name", song.name)
                s.put("artist", song.artist)
                s.put("album", song.album)
                s.put("duration", song.duration)
                s.put("trackNumber", song.trackNumber)
                s.put("importDate", song.importDate)
                libArray.put(s)
            }
            root.put("library", libArray)

            // Playlists
            val plArray = JSONArray()
            _playlists.value.forEach { playlist ->
                val p = JSONObject()
                p.put("id", playlist.id)
                p.put("name", playlist.name)
                val sUris = JSONArray()
                playlist.songs.forEach { sUris.put(it.uri) }
                p.put("songs", sUris)
                plArray.put(p)
            }
            root.put("playlists", plArray)

            // Favoritos
            val favArray = JSONArray()
            _favoritos.value.forEach { favArray.put(it) }
            root.put("favorites", favArray)

            // Histórico
            val histArray = JSONArray()
            _historico.value.forEach { histArray.put(it) }
            root.put("history", histArray)

            // Estatísticas
            val statsObj = JSONObject()
            _playCounts.value.forEach { (uri, count) ->
                statsObj.put(uri, count)
            }
            root.put("playCounts", statsObj)

            // Configurações
            val settings = JSONObject()
            settings.put("shuffle", _isShuffleEnabled.value)
            settings.put("repeat", _repeatMode.value.name)
            root.put("settings", settings)

            return root.toString(2)
        } catch (e: Exception) {
            Log.e("MobileDisco", "Erro ao exportar dados", e)
            return ""
        }
    }

    fun importarDados(jsonString: String): Boolean {
        try {
            val root = JSONObject(jsonString)
            
            // Importar Biblioteca
            val libArray = root.getJSONArray("library")
            val novasMusicas = mutableListOf<Song>()
            for (i in 0 until libArray.length()) {
                val s = libArray.getJSONObject(i)
                novasMusicas.add(Song(
                    uri = s.getString("uri"),
                    name = s.getString("name"),
                    artist = s.getString("artist"),
                    album = s.getString("album"),
                    duration = s.getLong("duration"),
                    trackNumber = s.getInt("trackNumber"),
                    importDate = s.optLong("importDate", System.currentTimeMillis()),
                    id = s.getString("uri").hashCode().toLong(),
                    cover = null
                ))
            }
            _biblioteca.value = novasMusicas
            // Persistir biblioteca
            novasMusicas.forEach { persistirMusica(it) }

            // Importar Favoritos
            val favArray = root.getJSONArray("favorites")
            val novosFavs = mutableSetOf<String>()
            for (i in 0 until favArray.length()) { novosFavs.add(favArray.getString(i)) }
            _favoritos.value = novosFavs
            prefs.edit().putStringSet("SETTINGS_FAVORITOS", novosFavs).apply()

            // Importar Histórico
            val histArray = root.getJSONArray("history")
            val novoHist = mutableListOf<String>()
            for (i in 0 until histArray.length()) { novoHist.add(histArray.getString(i)) }
            _historico.value = novoHist
            prefs.edit().putString("SETTINGS_HISTORICO", novoHist.joinToString(";;;")).apply()

            // Importar Estatísticas
            val statsObj = root.getJSONObject("playCounts")
            val novosCounts = mutableMapOf<String, Int>()
            val keys = statsObj.keys()
            val editor = prefs.edit()
            while (keys.hasNext()) {
                val uri = keys.next()
                val count = statsObj.getInt(uri)
                novosCounts[uri] = count
                editor.putInt("STATS_PLAY_COUNT_$uri", count)
            }
            _playCounts.value = novosCounts
            editor.apply()

            // Importar Playlists
            val plArray = root.getJSONArray("playlists")
            val novasPlaylists = mutableListOf<Playlist>()
            for (i in 0 until plArray.length()) {
                val p = plArray.getJSONObject(i)
                val sUris = p.getJSONArray("songs")
                val uris = mutableListOf<String>()
                for (j in 0 until sUris.length()) { uris.add(sUris.getString(j)) }
                
                val pSongs = uris.mapNotNull { uri -> novasMusicas.find { it.uri == uri } }
                novasPlaylists.add(Playlist(p.getLong("id"), p.getString("name"), pSongs))
            }
            _playlists.value = novasPlaylists
            playlistRepository.savePlaylists(novasPlaylists)

            // Importar Configurações
            val settings = root.getJSONObject("settings")
            val shuffle = settings.getBoolean("shuffle")
            val repeat = RepeatMode.valueOf(settings.getString("repeat"))
            
            _isShuffleEnabled.value = shuffle
            _repeatMode.value = repeat
            prefs.edit()
                .putBoolean("SETTINGS_SHUFFLE", shuffle)
                .putInt("SETTINGS_REPEAT", repeat.ordinal)
                .apply()

            // Atualiza metadados em segundo plano para carregar capas
            atualizarMetadadosEmSegundoPlano(getApplication())

            return true
        } catch (e: Exception) {
            Log.e("MobileDisco", "Erro ao importar dados", e)
            return false
        }
    }
}

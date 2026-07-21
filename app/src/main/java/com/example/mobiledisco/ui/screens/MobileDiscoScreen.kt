package com.example.mobiledisco.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobiledisco.player.PlaybackStatus
import com.example.mobiledisco.player.PlayerUiState
import com.example.mobiledisco.ui.navigation.AppScreen
import com.example.mobiledisco.ui.navigation.NavigationState
import com.example.mobiledisco.ui.theme.MobileDiscoTheme
import com.example.mobiledisco.viewmodel.MusicViewModel
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

@Composable
fun MobileDiscoScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val navigation = remember { NavigationState() }
    val viewModel: MusicViewModel = viewModel()
    
    val isPlaying by viewModel.isPlaying.collectAsState()
    val musicaSelecionada by viewModel.musicaSelecionada.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val favoritos by viewModel.favoritos.collectAsState()
    val isShuffleEnabled by viewModel.isShuffleEnabled.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    val json = viewModel.exportarDados()
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        OutputStreamWriter(outputStream).use { writer ->
                            writer.write(json)
                        }
                    }
                } catch (_: Exception) {
                    // Feedback visual via snackbar poderia ser adicionado aqui
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    context.contentResolver.openInputStream(it)?.use { inputStream ->
                        BufferedReader(InputStreamReader(inputStream)).use { reader ->
                            val json = reader.readText()
                            viewModel.importarDados(json)
                        }
                    }
                } catch (_: Exception) {
                    // Feedback visual
                }
            }
        }
    }

    Crossfade(targetState = navigation.currentScreen, label = "screenTransition") { screen ->
        when (screen) {
            AppScreen.LIBRARY -> {
                HomeScreenContent(
                    viewModel = viewModel,
                    onCoverClick = { navigation.openNowPlaying() },
                    onPlaylistClick = { playlist -> navigation.openPlaylist(playlist.id) },
                    onNavigateToPlaylist = { id -> navigation.openPlaylist(id) },
                    onOpenStatistics = { navigation.openStatistics() },
                    onOpenSettings = { navigation.openSettings() },
                    modifier = modifier
                )
            }
            AppScreen.NOW_PLAYING -> {
                BackHandler {
                    navigation.openLibrary()
                }
                NowPlayingScreen(
                    state = PlayerUiState(
                        musica = musicaSelecionada,
                        currentPosition = currentPosition,
                        duration = duration,
                        playbackStatus = if (isPlaying) PlaybackStatus.PLAYING else PlaybackStatus.STOPPED,
                        isShuffleEnabled = isShuffleEnabled,
                        repeatMode = repeatMode,
                        isFavorite = musicaSelecionada?.let { favoritos.contains(it.uri) } ?: false
                    ),
                    onEvent = { event -> viewModel.handlePlayerEvent(event) },
                    modifier = modifier
                )
            }
            AppScreen.PLAYLIST -> {
                val playlists by viewModel.playlists.collectAsState()
                val isEditingPlaylist by viewModel.isEditingPlaylist.collectAsState()
                val currentPlaylist = playlists.find { it.id == navigation.selectedPlaylistId }
                
                if (isEditingPlaylist != null) {
                    navigation.openLibrary()
                }

                if (currentPlaylist != null) {
                    PlaylistScreen(
                        playlist = currentPlaylist,
                        selectedSongId = musicaSelecionada?.id,
                        onSongClick = { playlist, song -> viewModel.selecionarMusicaDaPlaylist(playlist, song) },
                        onRemoveSong = { song -> viewModel.removerMusicaDaPlaylist(currentPlaylist.id, song) },
                        onAddSongsClick = {
                            viewModel.iniciarEdicaoPlaylist(currentPlaylist.id)
                        },
                        onBack = { navigation.openLibrary() },
                        modifier = modifier
                    )
                } else {
                    navigation.openLibrary()
                }
            }
            AppScreen.STATISTICS -> {
                val stats by viewModel.statistics.collectAsState()
                StatisticsScreen(
                    stats = stats,
                    onBack = { navigation.openLibrary() },
                    modifier = modifier
                )
            }
            AppScreen.SETTINGS -> {
                SettingsScreen(
                    onLimparHistorico = { viewModel.limparHistorico() },
                    onLimparFavoritos = { viewModel.limparFavoritos() },
                    onZerarEstatisticas = { viewModel.zerarEstatisticas() },
                    onExportarBiblioteca = { exportLauncher.launch("mobile_disco_backup.json") },
                    onImportarBiblioteca = { importLauncher.launch(arrayOf("application/json", "text/*")) },
                    onBack = { navigation.openLibrary() },
                    modifier = modifier
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MobileDiscoPreview() {
    MobileDiscoTheme {
        MobileDiscoScreen()
    }
}

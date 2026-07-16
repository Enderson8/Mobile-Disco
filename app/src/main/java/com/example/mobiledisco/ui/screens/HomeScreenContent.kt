package com.example.mobiledisco.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.mobiledisco.data.MusicMetadata
import com.example.mobiledisco.data.Playlist
import com.example.mobiledisco.data.Song
import com.example.mobiledisco.data.toSong
import com.example.mobiledisco.importer.MusicImporter
import com.example.mobiledisco.player.PlaybackStatus
import com.example.mobiledisco.player.PlayerEvent
import com.example.mobiledisco.player.PlayerUiState
import com.example.mobiledisco.ui.components.HiFiCard
import com.example.mobiledisco.ui.components.LibraryPanel
import com.example.mobiledisco.ui.components.PlayerPanel
import com.example.mobiledisco.ui.theme.HiFiColors
import com.example.mobiledisco.ui.theme.HiFiDimensions
import com.example.mobiledisco.viewmodel.MusicViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreenContent(
    viewModel: MusicViewModel,
    onCoverClick: () -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    val isPlaying by viewModel.isPlaying.collectAsState()
    val musicaSelecionada by viewModel.musicaSelecionada.collectAsState()
    val biblioteca by viewModel.biblioteca.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val isShuffleEnabled by viewModel.isShuffleEnabled.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            val metadata = MusicMetadata.read(context, it)
            val novaMusica = metadata.toSong(it)
            viewModel.adicionarMusica(novaMusica)
            viewModel.selecionarMusica(novaMusica)
        }
    }

    val folderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let { treeUri ->
            context.contentResolver.takePersistableUriPermission(
                treeUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            scope.launch {
                val musicasEncontradas = withContext(Dispatchers.IO) {
                    MusicImporter.importAlbum(context, treeUri)
                }

                if (musicasEncontradas.isNotEmpty()) {
                    viewModel.adicionarMusicas(musicasEncontradas)
                }
            }
        }
    }

    val onEvent: (PlayerEvent) -> Unit = { event ->
        viewModel.handlePlayerEvent(event)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = HiFiColors.Walnut900
    ) { padding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            color = HiFiColors.Walnut900
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = HiFiDimensions.Large),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Título Principal Estilizado
                Text(
                    text = "MOBILE DISCO",
                    style = MaterialTheme.typography.headlineSmall,
                    color = HiFiColors.Sand,
                    letterSpacing = 4.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(HiFiDimensions.Large))

                HiFiCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = HiFiDimensions.Medium)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val uiState = PlayerUiState(
                            musica = musicaSelecionada,
                            currentPosition = currentPosition,
                            duration = duration,
                            playbackStatus = if (isPlaying) PlaybackStatus.PLAYING else PlaybackStatus.STOPPED,
                            isShuffleEnabled = isShuffleEnabled,
                            repeatMode = repeatMode
                        )

                        PlayerPanel(
                            state = uiState,
                            onEvent = onEvent,
                            onCoverClick = onCoverClick
                        )

                        Spacer(modifier = Modifier.height(HiFiDimensions.ExtraLarge))

                        // Botão Escolher Música
                        Button(
                            onClick = { launcher.launch(arrayOf("audio/*")) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HiFiColors.Copper,
                                contentColor = HiFiColors.Ivory
                            ),
                            shape = RoundedCornerShape(HiFiDimensions.Small)
                        ) {
                            Text("ESCOLHER MÚSICA", letterSpacing = 1.sp)
                        }

                        Spacer(modifier = Modifier.height(HiFiDimensions.Medium))

                        // Botão Importar Álbum
                        Button(
                            onClick = { folderLauncher.launch(null) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HiFiColors.Copper,
                                contentColor = HiFiColors.Ivory
                            ),
                            shape = RoundedCornerShape(HiFiDimensions.Small)
                        ) {
                            Text("IMPORTAR ÁLBUM", letterSpacing = 1.sp)
                        }

                        Spacer(modifier = Modifier.height(HiFiDimensions.Medium))

                        // Botão Limpar Biblioteca
                        Button(
                            onClick = {
                                viewModel.limparBiblioteca()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HiFiColors.Walnut700,
                                contentColor = HiFiColors.Sand
                            ),
                            shape = RoundedCornerShape(HiFiDimensions.Small)
                        ) {
                            Text("LIMPAR BIBLIOTECA", letterSpacing = 1.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(HiFiDimensions.ExtraLarge))

                HorizontalDivider(
                    thickness = HiFiDimensions.BorderWidth,
                    color = HiFiColors.Divider,
                    modifier = Modifier.padding(horizontal = HiFiDimensions.Large)
                )

                Spacer(modifier = Modifier.height(HiFiDimensions.Large))

                LibraryPanel(
                    songs = biblioteca,
                    playlists = playlists,
                    selectedSongId = musicaSelecionada?.id,
                    onSongClick = viewModel::selecionarMusica,
                    onPlaylistClick = onPlaylistClick,
                    onCriarPlaylist = viewModel::criarPlaylist,
                    onRenomearPlaylist = viewModel::renomearPlaylist,
                    onRemoverPlaylist = viewModel::removerPlaylist,
                    onAddSongToPlaylist = viewModel::adicionarMusicaNaPlaylist,
                    onShowSnackbar = { message ->
                        scope.launch {
                            snackbarHostState.showSnackbar(message)
                        }
                    }
                )
            }
        }
    }
}


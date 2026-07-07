package com.example.mobiledisco.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobiledisco.data.MusicMetadata
import com.example.mobiledisco.data.Song
import com.example.mobiledisco.data.toSong
import com.example.mobiledisco.player.PlaybackStatus
import com.example.mobiledisco.player.PlayerEvent
import com.example.mobiledisco.player.PlayerUiState
import com.example.mobiledisco.ui.components.HiFiCard
import com.example.mobiledisco.ui.components.LibraryPanel
import com.example.mobiledisco.ui.components.PlayerPanel
import com.example.mobiledisco.ui.theme.HiFiColors
import com.example.mobiledisco.ui.theme.HiFiDimensions
import com.example.mobiledisco.ui.theme.MobileDiscoTheme
import com.example.mobiledisco.viewmodel.MusicViewModel
import kotlinx.coroutines.delay

@Composable
fun MobileDiscoScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: MusicViewModel = viewModel()
    val player = viewModel.player
    val isPlaying by viewModel.isPlaying.collectAsState()
    val musicaSelecionada by viewModel.musicaSelecionada.collectAsState()

    val biblioteca by viewModel.biblioteca.collectAsState()

    var currentPosition by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = player.getCurrentPosition()
            duration = player.getDuration()
            delay(1000)
        }
    }

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

    val onEvent: (PlayerEvent) -> Unit = { event ->
        when (event) {
            PlayerEvent.PlayPause -> {
                musicaSelecionada?.let {
                    viewModel.toggle(Uri.parse(it.uri))
                }
            }
            PlayerEvent.Stop -> {
                viewModel.stop()
                currentPosition = 0L
            }
            PlayerEvent.Next -> {
                viewModel.proximaMusica()
            }
            PlayerEvent.Previous -> {
                viewModel.anteriorMusica()
            }
            is PlayerEvent.Seek -> {
                player.seekTo(event.position)
                currentPosition = event.position
            }
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
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
                        playbackStatus = if (isPlaying) PlaybackStatus.PLAYING else PlaybackStatus.STOPPED
                    )

                    PlayerPanel(
                        state = uiState,
                        onEvent = onEvent
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

                    // Botão Limpar Biblioteca
                    Button(
                        onClick = {
                            viewModel.limparBiblioteca()
                            currentPosition = 0L
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
                selectedSongId = musicaSelecionada?.id,
                onSongClick = viewModel::selecionarMusica
            )
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

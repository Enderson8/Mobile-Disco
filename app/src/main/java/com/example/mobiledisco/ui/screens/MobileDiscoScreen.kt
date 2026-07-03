package com.example.mobiledisco.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobiledisco.data.MusicMetadata
import com.example.mobiledisco.data.Song
import com.example.mobiledisco.player.PlayerEvent
import com.example.mobiledisco.player.PlayerUiState
import com.example.mobiledisco.ui.components.PlayerPanel
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
            val novaMusica = Song(
                name = metadata.title,
                artist = metadata.artist,
                album = metadata.album,
                duration = metadata.duration,
                uri = it.toString(),
                id = System.currentTimeMillis(),
                cover = metadata.cover
            )
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Mobile Disco",
            style = MaterialTheme.typography.headlineMedium
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                val uiState = PlayerUiState(
                    musica = musicaSelecionada,
                    currentPosition = currentPosition,
                    duration = duration,
                    isPlaying = isPlaying
                )

                PlayerPanel(
                    state = uiState,
                    onEvent = onEvent
                )

                Spacer(modifier = Modifier.height(30.dp))

                Button(onClick = { launcher.launch(arrayOf("audio/*")) }) {
                    Text("Escolher música")
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        viewModel.limparBiblioteca()
                        currentPosition = 0L
                    }
                ) {
                    Text("🗑 Limpar biblioteca")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Biblioteca",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.height(200.dp)) {
            items(biblioteca) { musica ->
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            viewModel.selecionarMusica(musica)
                        }
                ) {
                    Text(
                        text = if (musicaSelecionada?.id == musica.id)
                            "▶ ${musica.name}"
                        else
                            musica.name,
                        style = if (musicaSelecionada?.id == musica.id)
                            MaterialTheme.typography.titleMedium
                        else
                            MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = musica.artist,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

fun getFileName(context: Context, uri: Uri): String {
    var name = "Música desconhecida"
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (it.moveToFirst() && nameIndex >= 0) {
            name = it.getString(nameIndex)
        }
    }
    return name
}

fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

@Preview(showBackground = true)
@Composable
fun MobileDiscoPreview() {
    MobileDiscoTheme {
        MobileDiscoScreen()
    }
}

package com.example.mobiledisco

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobiledisco.ui.theme.MobileDiscoTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.delay
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import android.provider.OpenableColumns
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileDiscoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MobileDiscoScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

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

        Spacer(modifier = Modifier.height(40.dp))

        musicaSelecionada?.cover?.let { bytes ->
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Capa do álbum",
                modifier = Modifier.size(220.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        Text(text = musicaSelecionada?.name ?: "Nenhuma música selecionada")

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "${formatTime(currentPosition)} / ${formatTime(duration)}")

        Slider(
            value = currentPosition.toFloat(),
            onValueChange = { novoValor ->
                player.seekTo(novoValor.toLong())
                currentPosition = novoValor.toLong()
            },
            valueRange = 0f..duration.toFloat().coerceAtLeast(0f),
            enabled = duration > 0
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

        Spacer(modifier = Modifier.height(20.dp))

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

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                musicaSelecionada?.let {
                    viewModel.toggle(
                        Uri.parse(it.uri)
                    )
                }
            }
        ) {
            Text(if (isPlaying) "⏸ Pause" else "▶ Play")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            viewModel.stop()
            currentPosition = 0L
        }) {
            Text("⏹ Stop")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                viewModel.anteriorMusica()
            }) {
                Text("⏮")
            }

            Spacer(modifier = Modifier.width(30.dp))

            Button(onClick = {
                viewModel.proximaMusica()
            }) {
                Text("⏭")
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

package com.example.mobiledisco

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import android.provider.OpenableColumns
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import android.content.SharedPreferences


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            MobileDiscoTheme {

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    MobileDiscoScreen(
                        modifier = Modifier.padding(innerPadding)
                    )

                }
            }
        }
    }
}


@Composable
fun MobileDiscoScreen(
    modifier: Modifier = Modifier
) {

    var musicaSelecionada by remember {
        mutableStateOf<Song?>(null)
    }

    var biblioteca by remember {
        mutableStateOf(listOf<Song>())
    }

    var musicaAtualIndex by remember {
        mutableStateOf(0)
    }

    val context = LocalContext.current
    val player = remember(context) { MusicPlayer(context) }

    val prefs = remember {
        context.getSharedPreferences(
            "mobile_disco",
            Context.MODE_PRIVATE
        )
    }

    LaunchedEffect(Unit) {
        val musicasSalvas = prefs.all
        biblioteca = musicasSalvas.map { item ->
            Song(
                name = item.key,
                uri = item.value.toString(),
                id = System.currentTimeMillis() // Temporário para carregar
            )
        }
        musicaSelecionada = null
    }

    var isPlaying by remember {
        mutableStateOf(false)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            val nome = getFileName(context, it)
            val novaMusica = Song(
                nome,
                it.toString(),
                id = System.currentTimeMillis()
            )
            biblioteca = biblioteca + novaMusica
            musicaSelecionada = novaMusica

            prefs.edit()
                .putString(
                    novaMusica.name,
                    novaMusica.uri
                )
                .apply()
        }
    }

    Column(

        modifier = modifier.fillMaxSize(),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center

    ) {


        Text(

            text = "Mobile Disco",

            style = MaterialTheme.typography.headlineMedium

        )


        Spacer(
            modifier = Modifier.height(40.dp)
        )


        Text(
            text = musicaSelecionada?.name ?: "Nenhuma música selecionada"
        )

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        Button(
            onClick = {
                launcher.launch(arrayOf("audio/*"))
            }
        ) {

            Text("Escolher música")

        }


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        Button(
            onClick = {

                biblioteca = emptyList()

                musicaSelecionada = null

                musicaAtualIndex = 0

                isPlaying = false

                player.stop()

                prefs.edit()
                    .clear()
                    .apply()

            }
        ) {

            Text("🗑 Limpar biblioteca")

        }


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        LazyColumn(
            modifier = Modifier.height(200.dp)
        ) {

            items(biblioteca) { musica ->

                Text(
                    text = musica.name,
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            musicaSelecionada = musica
                            musicaAtualIndex = biblioteca.indexOf(musica)
                            player.play(Uri.parse(musica.uri))
                            isPlaying = true
                        }
                )

            }

        }


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        Button(
            onClick = {
                musicaSelecionada?.let {
                    player.togglePlayback(Uri.parse(it.uri))
                    isPlaying = !isPlaying
                }
            }
        ) {

            Text(
                if (isPlaying) "⏸ Pause" else "▶ Play"
            )

        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Button(
            onClick = {
                player.stop()
                isPlaying = false
            }
        ) {
            Text("⏹ Stop")
        }


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {


            Button(
                onClick = {
                    if (musicaAtualIndex > 0) {
                        musicaAtualIndex--
                        val musicaAnterior = biblioteca[musicaAtualIndex]
                        musicaSelecionada = musicaAnterior
                        player.play(Uri.parse(musicaAnterior.uri))
                        isPlaying = true
                    }
                }
            ) {

                Text("⏮")

            }


            Spacer(
                modifier = Modifier.width(30.dp)
            )


            Button(
                onClick = {
                    if (musicaAtualIndex < biblioteca.size - 1) {
                        musicaAtualIndex++
                        val proximaMusica = biblioteca[musicaAtualIndex]
                        musicaSelecionada = proximaMusica
                        player.play(Uri.parse(proximaMusica.uri))
                        isPlaying = true
                    }
                }
            ) {

                Text("⏭")

            }

        }
    }
}

fun getFileName(
    context: Context,
    uri: Uri
): String {

    var name = "Música desconhecida"

    val cursor = context.contentResolver.query(
        uri,
        null,
        null,
        null,
        null
    )

    cursor?.use {

        val nameIndex =
            it.getColumnIndex(OpenableColumns.DISPLAY_NAME)

        if (it.moveToFirst() && nameIndex >= 0) {

            name = it.getString(nameIndex)

        }
    }

    return name
}

@Preview(showBackground = true)
@Composable
fun MobileDiscoPreview() {

    MobileDiscoTheme {

        MobileDiscoScreen()

    }
}

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
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import android.provider.OpenableColumns
import android.content.Context



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

    val context = LocalContext.current
    val player = remember { MusicPlayer(context) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val nome = getFileName(context, it)
            musicaSelecionada = Song(nome, it.toString())
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
                launcher.launch("audio/*")
            }
        ) {

            Text("Escolher música")

        }


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        Button(
            onClick = {
                musicaSelecionada?.let {
                    player.play(Uri.parse(it.uri))
                }
            }
        ) {

            Text("▶ Play")

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

                }
            ) {

                Text("⏮")

            }


            Spacer(
                modifier = Modifier.width(30.dp)
            )


            Button(
                onClick = {

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

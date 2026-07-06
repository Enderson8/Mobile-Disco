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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ExposedDropdownMenuAnchorType
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobiledisco.data.MusicMetadata
import com.example.mobiledisco.data.Song
import com.example.mobiledisco.data.toSong
import com.example.mobiledisco.player.PlaybackStatus
import com.example.mobiledisco.player.PlayerEvent
import com.example.mobiledisco.player.PlayerUiState
import com.example.mobiledisco.ui.components.HiFiCard
import com.example.mobiledisco.ui.components.PlayerPanel
import com.example.mobiledisco.ui.components.SongListItem
import com.example.mobiledisco.ui.state.SortOption
import com.example.mobiledisco.ui.theme.HiFiColors
import com.example.mobiledisco.ui.theme.HiFiDimensions
import com.example.mobiledisco.ui.theme.MobileDiscoTheme
import com.example.mobiledisco.utils.formatTime
import com.example.mobiledisco.viewmodel.MusicViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
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

    var searchText by remember { mutableStateOf("") }
    var sortOption by remember { mutableStateOf(SortOption.NAME) }
    var expanded by remember { mutableStateOf(false) }

    val filteredSongs = biblioteca.filter {
        it.name.contains(searchText, ignoreCase = true) ||
                it.artist.contains(searchText, ignoreCase = true)
    }

    val displayedSongs = when (sortOption) {
        SortOption.NAME -> filteredSongs.sortedBy { it.name }
        SortOption.ARTIST -> filteredSongs.sortedBy { it.artist }
        SortOption.ALBUM -> filteredSongs.sortedBy { it.album }
    }

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
        color = HiFiColors.WarmBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Mobile Disco",
                style = MaterialTheme.typography.headlineMedium
            )

            HiFiCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(HiFiDimensions.Medium)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(HiFiDimensions.CardPadding))

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

                    Button(onClick = { launcher.launch(arrayOf("audio/*")) }) {
                        Text("Escolher música")
                    }

                    Spacer(modifier = Modifier.height(HiFiDimensions.CardPadding))

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

            Spacer(modifier = Modifier.height(HiFiDimensions.Large))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(HiFiDimensions.Medium))

            Text(
                text = "Biblioteca",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = if (searchText.isEmpty())
                    "${biblioteca.size} músicas"
                else
                    "${displayedSongs.size} resultados",
                style = MaterialTheme.typography.bodySmall,
                color = HiFiColors.PanelGray
            )

            Spacer(modifier = Modifier.height(HiFiDimensions.Small))

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Pesquisar na biblioteca") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HiFiDimensions.Medium),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(HiFiDimensions.Small))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HiFiDimensions.Medium)
            ) {
                OutlinedTextField(
                    value = when(sortOption) {
                        SortOption.NAME -> "Nome"
                        SortOption.ARTIST -> "Artista"
                        SortOption.ALBUM -> "Álbum"
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Ordenar por") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Nome") },
                        onClick = {
                            sortOption = SortOption.NAME
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Artista") },
                        onClick = {
                            sortOption = SortOption.ARTIST
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Álbum") },
                        onClick = {
                            sortOption = SortOption.ALBUM
                            expanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(HiFiDimensions.Normal))

            if (displayedSongs.isEmpty()) {
                Text(
                    text = "Nenhuma música encontrada.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = HiFiColors.PanelGray,
                    modifier = Modifier.padding(vertical = HiFiDimensions.Large)
                )
            } else {
                LazyColumn(modifier = Modifier.height(HiFiDimensions.LibraryListHeight)) {
                    items(displayedSongs) { musica ->
                        SongListItem(
                            song = musica,
                            isSelected = musicaSelecionada?.id == musica.id,
                            onClick = {
                                viewModel.selecionarMusica(musica)
                            }
                        )
                    }
                }
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

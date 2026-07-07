package com.example.mobiledisco.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.mobiledisco.data.Song
import com.example.mobiledisco.ui.state.SortOption
import com.example.mobiledisco.ui.theme.HiFiColors
import com.example.mobiledisco.ui.theme.HiFiDimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryPanel(
    songs: List<Song>,
    selectedSongId: Long?,
    onSongClick: (Song) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var sortOption by remember { mutableStateOf(SortOption.NAME) }
    var expanded by remember { mutableStateOf(false) }

    val filteredSongs = songs.filter {
        it.name.contains(searchText, ignoreCase = true) ||
                it.artist.contains(searchText, ignoreCase = true)
    }

    val displayedSongs = when (sortOption) {
        SortOption.NAME -> filteredSongs.sortedBy { it.name }
        SortOption.ARTIST -> filteredSongs.sortedBy { it.artist }
        SortOption.ALBUM -> filteredSongs.sortedBy { it.album }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Biblioteca",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = if (searchText.isEmpty())
                "${songs.size} músicas"
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
                value = when (sortOption) {
                    SortOption.NAME -> "Nome"
                    SortOption.ARTIST -> "Artista"
                    SortOption.ALBUM -> "Álbum"
                },
                onValueChange = {},
                readOnly = true,
                label = { Text("Ordenar por") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                    .fillMaxWidth()
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
                        isSelected = selectedSongId == musica.id,
                        onClick = {
                            onSongClick(musica)
                        }
                    )
                }
            }
        }
    }
}

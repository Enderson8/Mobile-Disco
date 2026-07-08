package com.example.mobiledisco.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobiledisco.data.Song
import com.example.mobiledisco.data.toAlbums
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
    
    // Estado para controlar quais álbuns estão expandidos (pelo nome + artista)
    var expandedAlbums by remember { mutableStateOf(setOf<String>()) }

    val filteredSongs = songs.filter {
        it.name.contains(searchText, ignoreCase = true) ||
                it.artist.contains(searchText, ignoreCase = true) ||
                it.album.contains(searchText, ignoreCase = true)
    }

    val displayedAlbums = remember(filteredSongs, sortOption) {
        val albums = filteredSongs.toAlbums()
        when (sortOption) {
            SortOption.NAME -> albums.sortedBy { it.name.lowercase() }
            SortOption.ARTIST -> albums.sortedBy { it.artist.lowercase() }
            SortOption.ALBUM -> albums.sortedBy { it.name.lowercase() }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Cabeçalho (Estilo label Hi-Fi)
        Text(
            text = "MUSIC LIBRARY",
            style = MaterialTheme.typography.labelSmall,
            color = HiFiColors.Sand,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(HiFiDimensions.Tiny))

        // 2. Contador (Informação secundária)
        Text(
            text = if (searchText.isEmpty())
                "${songs.size} músicas"
            else
                "${displayedAlbums.size} resultados",
            style = MaterialTheme.typography.bodySmall,
            color = HiFiColors.Sand
        )

        Spacer(modifier = Modifier.height(HiFiDimensions.Large))

        // 3. Campo de Pesquisa
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Pesquisar na biblioteca...", color = HiFiColors.Sand) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = HiFiColors.Copper
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HiFiDimensions.Medium),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = HiFiColors.Ivory,
                unfocusedTextColor = HiFiColors.Ivory,
                focusedContainerColor = HiFiColors.DarkPanel,
                unfocusedContainerColor = HiFiColors.Espresso,
                cursorColor = HiFiColors.Copper,
                focusedBorderColor = HiFiColors.Copper,
                unfocusedBorderColor = HiFiColors.CopperDark,
                focusedLabelColor = HiFiColors.Copper,
                unfocusedLabelColor = HiFiColors.Sand,
                focusedPlaceholderColor = HiFiColors.Sand,
                unfocusedPlaceholderColor = HiFiColors.SoftBrown
            )
        )

        Spacer(modifier = Modifier.height(HiFiDimensions.Medium))

        // 4. Seletor de Ordenação
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HiFiDimensions.Medium)
        ) {
            OutlinedTextField(
                value = when (sortOption) {
                    SortOption.NAME -> "Música"
                    SortOption.ARTIST -> "Artista"
                    SortOption.ALBUM -> "Álbum"
                },
                onValueChange = {},
                readOnly = true,
                label = { Text("Ordenar por") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = HiFiColors.Ivory,
                    unfocusedTextColor = HiFiColors.Ivory,
                    focusedContainerColor = HiFiColors.DarkPanel,
                    unfocusedContainerColor = HiFiColors.Espresso,
                    cursorColor = HiFiColors.Copper,
                    focusedBorderColor = HiFiColors.Copper,
                    unfocusedBorderColor = HiFiColors.CopperDark,
                    focusedLabelColor = HiFiColors.Copper,
                    unfocusedLabelColor = HiFiColors.Sand
                ),
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(HiFiColors.DarkPanel)
            ) {
                DropdownMenuItem(
                    text = { Text("Música", color = HiFiColors.Ivory) },
                    onClick = {
                        sortOption = SortOption.NAME
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Artista", color = HiFiColors.Ivory) },
                    onClick = {
                        sortOption = SortOption.ARTIST
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Álbum", color = HiFiColors.Ivory) },
                    onClick = {
                        sortOption = SortOption.ALBUM
                        expanded = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(HiFiDimensions.ExtraLarge))

        // Divisor principal do catálogo
        HorizontalDivider(
            thickness = HiFiDimensions.BorderWidth,
            color = HiFiColors.Divider
        )

        // 5. Estado da Lista Animado
        AnimatedContent(
            targetState = displayedAlbums,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "libraryFade"
        ) { targetAlbums ->
            if (songs.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = HiFiDimensions.ExtraLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sua biblioteca está vazia.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HiFiColors.Sand,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(HiFiDimensions.Small))
                    Text(
                        text = "Adicione músicas usando o botão acima.",
                        style = MaterialTheme.typography.bodySmall,
                        color = HiFiColors.Sand,
                        textAlign = TextAlign.Center
                    )
                }
            } else if (targetAlbums.isEmpty()) {
                Text(
                    text = "Nenhuma música encontrada.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = HiFiColors.Sand,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = HiFiDimensions.ExtraLarge),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(modifier = Modifier.height(HiFiDimensions.LibraryListHeight)) {
                    targetAlbums.forEach { album ->
                        val albumId = "${album.name}-${album.artist}"
                        val isExpanded = expandedAlbums.contains(albumId)

                        item(key = albumId) {
                            AlbumListItem(
                                album = album,
                                isExpanded = isExpanded,
                                onClick = {
                                    expandedAlbums = if (isExpanded) {
                                        expandedAlbums - albumId
                                    } else {
                                        expandedAlbums + albumId
                                    }
                                }
                            )
                        }

                        if (isExpanded) {
                            items(album.songs, key = { it.uri }) { song ->
                                SongListItem(
                                    modifier = Modifier.padding(start = 32.dp),
                                    song = song,
                                    isSelected = selectedSongId == song.id,
                                    onClick = {
                                        onSongClick(song)
                                    }
                                )
                            }
                        }

                        item {
                            HorizontalDivider(
                                thickness = HiFiDimensions.BorderWidth,
                                color = HiFiColors.Divider
                            )
                        }
                    }
                }
            }
        }
    }
}

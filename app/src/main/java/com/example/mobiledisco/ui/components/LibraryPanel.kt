package com.example.mobiledisco.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobiledisco.data.Playlist
import com.example.mobiledisco.data.Song
import com.example.mobiledisco.data.toAlbums
import com.example.mobiledisco.ui.state.FilterOption
import com.example.mobiledisco.ui.state.SortDirection
import com.example.mobiledisco.ui.state.SortField
import com.example.mobiledisco.ui.state.SortOrder
import com.example.mobiledisco.ui.theme.HiFiColors
import com.example.mobiledisco.ui.theme.HiFiDimensions

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun LibraryPanel(
    songs: List<Song>,
    playlists: List<Playlist>,
    favorites: Set<String>,
    history: List<Song>,
    mostPlayed: List<Pair<Song, Int>>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedSongId: Long?,
    isEditingPlaylist: Long? = null,
    sortOrder: SortOrder = SortOrder(),
    onSortOrderChange: (SortOrder) -> Unit = {},
    filterOption: FilterOption = FilterOption.ALL,
    onFilterOptionChange: (FilterOption) -> Unit = {},
    onSongClick: (Song) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onCriarPlaylist: (String) -> Unit,
    onRenomearPlaylist: (Long, String) -> Unit,
    onRemoverPlaylist: (Long) -> Unit,
    onAddSongToPlaylist: (Long, Song) -> Boolean,
    onConcluirEdicao: () -> Unit,
    onOpenStatistics: () -> Unit,
    onOpenSettings: () -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    var expandedSort by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }
    
    var showAddToPlaylistDialog by remember { mutableStateOf(false) }
    var selectedSongToPlaylist by remember { mutableStateOf<Song?>(null) }

    var showManagePlaylistDialog by remember { mutableStateOf(false) }
    var selectedPlaylistToManage by remember { mutableStateOf<Playlist?>(null) }
    
    var showRenameDialog by remember { mutableStateOf(false) }
    var renamedPlaylistName by remember { mutableStateOf("") }
    
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    
    var expandedAlbums by remember { mutableStateOf(setOf<String>()) }
    
    var expandedFavorites by remember { mutableStateOf(false) }
    var expandedHistory by remember { mutableStateOf(false) }
    var expandedMostPlayed by remember { mutableStateOf(false) }

    val favoriteSongs = remember(songs, favorites) {
        songs.filter { favorites.contains(it.uri) }
    }

    val displayedAlbums = remember(songs, sortOrder) {
        val albums = songs.toAlbums()
        val sorted = when (sortOrder.field) {
            SortField.NAME -> albums.sortedBy { it.name.lowercase() }
            SortField.ARTIST -> albums.sortedBy { it.artist.lowercase() }
            SortField.ALBUM -> albums.sortedBy { it.name.lowercase() }
            SortField.IMPORT_DATE -> albums.sortedBy { it.songs.maxOfOrNull { s -> s.importDate } ?: 0L }
            SortField.MOST_PLAYED -> albums.sortedByDescending { it.songs.sumOf { s -> 0 } } // Simplificado
        }
        if (sortOrder.direction == SortDirection.DESCENDING) sorted.reversed() else sorted
    }

    val showHistory = filterOption == FilterOption.ALL || filterOption == FilterOption.HISTORY
    val showFavorites = filterOption == FilterOption.ALL || filterOption == FilterOption.FAVORITES
    val showMostPlayed = filterOption == FilterOption.ALL || filterOption == FilterOption.MOST_PLAYED
    val showPlaylists = filterOption == FilterOption.ALL || filterOption == FilterOption.PLAYLISTS
    val showAlbums = filterOption == FilterOption.ALL || filterOption == FilterOption.ALBUMS

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- PESQUISA GLOBAL ---
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Pesquisar em tudo...", color = HiFiColors.Sand) },
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
                unfocusedBorderColor = HiFiColors.CopperDark
            )
        )

        Spacer(modifier = Modifier.height(HiFiDimensions.Medium))

        // --- FILTROS RÁPIDOS ---
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HiFiDimensions.Medium),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChipItem(
                    label = "Tudo",
                    isSelected = filterOption == FilterOption.ALL,
                    onClick = { onFilterOptionChange(FilterOption.ALL) },
                    icon = Icons.Default.LibraryMusic
                )
            }
            item {
                FilterChipItem(
                    label = "Favoritos",
                    isSelected = filterOption == FilterOption.FAVORITES,
                    onClick = { onFilterOptionChange(FilterOption.FAVORITES) },
                    icon = Icons.Default.Favorite
                )
            }
            item {
                FilterChipItem(
                    label = "Playlists",
                    isSelected = filterOption == FilterOption.PLAYLISTS,
                    onClick = { onFilterOptionChange(FilterOption.PLAYLISTS) },
                    icon = Icons.Default.PlaylistPlay
                )
            }
            item {
                FilterChipItem(
                    label = "Álbuns",
                    isSelected = filterOption == FilterOption.ALBUMS,
                    onClick = { onFilterOptionChange(FilterOption.ALBUMS) },
                    icon = Icons.Default.MusicNote
                )
            }
            item {
                FilterChipItem(
                    label = "Histórico",
                    isSelected = filterOption == FilterOption.HISTORY,
                    onClick = { onFilterOptionChange(FilterOption.HISTORY) },
                    icon = Icons.Default.History
                )
            }
            item {
                FilterChipItem(
                    label = "Populares",
                    isSelected = filterOption == FilterOption.MOST_PLAYED,
                    onClick = { onFilterOptionChange(FilterOption.MOST_PLAYED) },
                    icon = Icons.Default.LocalFireDepartment
                )
            }
        }

        Spacer(modifier = Modifier.height(HiFiDimensions.Medium))

        // --- CABEÇALHO DE EDIÇÃO ---
        if (isEditingPlaylist != null) {
            Surface(
                color = HiFiColors.DarkPanel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(HiFiDimensions.Medium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Selecione músicas para adicionar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HiFiColors.Sand
                    )
                    Button(
                        onClick = onConcluirEdicao,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HiFiColors.Copper,
                            contentColor = HiFiColors.Ivory
                        ),
                        shape = RoundedCornerShape(HiFiDimensions.Small)
                    ) {
                        Text("CONCLUIR")
                    }
                }
            }
            Spacer(modifier = Modifier.height(HiFiDimensions.Medium))
        }

        // --- SEÇÕES FIXAS (Só se "Tudo" ou filtro específico) ---
        if (filterOption == FilterOption.ALL) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HiFiDimensions.Medium)
                    .clickable { onOpenStatistics() }
                    .padding(vertical = HiFiDimensions.Medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.BarChart, null, tint = HiFiColors.Copper, modifier = Modifier.size(24.dp))
                Text("📊 ESTATÍSTICAS", style = MaterialTheme.typography.labelSmall, color = HiFiColors.Ivory, letterSpacing = 2.sp, modifier = Modifier.padding(start = HiFiDimensions.Medium))
                Spacer(Modifier.weight(1f))
                Text("▶", color = HiFiColors.Copper, style = MaterialTheme.typography.bodySmall)
            }
            HorizontalDivider(thickness = HiFiDimensions.BorderWidth, color = HiFiColors.Divider)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HiFiDimensions.Medium)
                    .clickable { onOpenSettings() }
                    .padding(vertical = HiFiDimensions.Medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Settings, null, tint = HiFiColors.Copper, modifier = Modifier.size(24.dp))
                Text("⚙ CONFIGURAÇÕES", style = MaterialTheme.typography.labelSmall, color = HiFiColors.Ivory, letterSpacing = 2.sp, modifier = Modifier.padding(start = HiFiDimensions.Medium))
                Spacer(Modifier.weight(1f))
                Text("▶", color = HiFiColors.Copper, style = MaterialTheme.typography.bodySmall)
            }
            HorizontalDivider(thickness = HiFiDimensions.BorderWidth, color = HiFiColors.Divider)
        }

        // --- HISTÓRICO ---
        if (showHistory && history.isNotEmpty()) {
            SectionHeader("🕒 ÚLTIMAS REPRODUZIDAS")
            SectionItem(
                title = "Histórico",
                subtitle = "${history.size} músicas",
                icon = Icons.Default.History,
                isExpanded = expandedHistory,
                onToggle = { expandedHistory = !expandedHistory }
            ) {
                history.forEach { song ->
                    SongListItem(song = song, isSelected = selectedSongId == song.id, onClick = { onSongClick(song) }, modifier = Modifier.padding(start = 32.dp))
                }
            }
        }

        // --- FAVORITOS ---
        if (showFavorites && favoriteSongs.isNotEmpty()) {
            SectionHeader("⭐ FAVORITOS")
            SectionItem(
                title = "Músicas Favoritas",
                subtitle = "${favoriteSongs.size} músicas",
                icon = Icons.Default.Favorite,
                isExpanded = expandedFavorites,
                onToggle = { expandedFavorites = !expandedFavorites }
            ) {
                favoriteSongs.forEach { song ->
                    SongListItem(song = song, isSelected = selectedSongId == song.id, onClick = { onSongClick(song) }, modifier = Modifier.padding(start = 32.dp))
                }
            }
        }

        // --- MAIS TOCADAS ---
        if (showMostPlayed && mostPlayed.isNotEmpty()) {
            SectionHeader("🔥 MAIS TOCADAS")
            SectionItem(
                title = "Mais Tocadas",
                subtitle = "${mostPlayed.size} faixas populares",
                icon = Icons.Default.LocalFireDepartment,
                isExpanded = expandedMostPlayed,
                onToggle = { expandedMostPlayed = !expandedMostPlayed }
            ) {
                mostPlayed.forEach { (song, count) ->
                    SongListItem(song = song, isSelected = selectedSongId == song.id, onClick = { onSongClick(song) }, modifier = Modifier.padding(start = 32.dp), label = "$count")
                }
            }
        }

        // --- PLAYLISTS ---
        if (showPlaylists) {
            SectionHeader("PLAYLISTS")
            playlists.forEach { playlist ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(onClick = { onPlaylistClick(playlist) }, onLongClick = { selectedPlaylistToManage = playlist; showManagePlaylistDialog = true })
                        .padding(horizontal = HiFiDimensions.Medium, vertical = HiFiDimensions.Small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.PlaylistPlay, null, tint = HiFiColors.Copper, modifier = Modifier.padding(end = HiFiDimensions.Medium))
                    Column {
                        Text(playlist.name, style = MaterialTheme.typography.titleMedium, color = HiFiColors.Ivory)
                        Text("${playlist.songs.size} músicas", style = MaterialTheme.typography.bodySmall, color = HiFiColors.Sand)
                    }
                }
                HorizontalDivider(thickness = HiFiDimensions.BorderWidth, color = HiFiColors.Divider, modifier = Modifier.padding(horizontal = HiFiDimensions.Medium))
            }
            Spacer(Modifier.height(HiFiDimensions.Medium))
            Button(onClick = { showCreateDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = HiFiColors.Walnut700, contentColor = HiFiColors.Ivory), shape = RoundedCornerShape(HiFiDimensions.Small)) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(HiFiDimensions.Small))
                Text("NOVA PLAYLIST", letterSpacing = 1.sp)
            }
        }

        // --- ÁLBUNS / BIBLIOTECA ---
        if (showAlbums) {
            SectionHeader("MUSIC LIBRARY")
            Text(if (searchQuery.isEmpty()) "${songs.size} músicas" else "${displayedAlbums.size} resultados", style = MaterialTheme.typography.bodySmall, color = HiFiColors.Sand)
            Spacer(Modifier.height(HiFiDimensions.Large))

            // Seletor de Ordenação
            ExposedDropdownMenuBox(
                expanded = expandedSort,
                onExpandedChange = { expandedSort = !expandedSort },
                modifier = Modifier.fillMaxWidth().padding(horizontal = HiFiDimensions.Medium)
            ) {
                OutlinedTextField(
                    value = getSortLabel(sortOrder),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Ordenar por") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSort) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = HiFiColors.Ivory,
                        unfocusedTextColor = HiFiColors.Ivory,
                        focusedContainerColor = HiFiColors.DarkPanel,
                        unfocusedContainerColor = HiFiColors.Espresso,
                        cursorColor = HiFiColors.Copper,
                        focusedBorderColor = HiFiColors.Copper,
                        unfocusedBorderColor = HiFiColors.CopperDark
                    ),
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedSort,
                    onDismissRequest = { expandedSort = false },
                    modifier = Modifier.background(HiFiColors.DarkPanel)
                ) {
                    SortMenuItem("Nome", SortField.NAME, sortOrder, onSortOrderChange) { expandedSort = false }
                    SortMenuItem("Artista", SortField.ARTIST, sortOrder, onSortOrderChange) { expandedSort = false }
                    SortMenuItem("Álbum", SortField.ALBUM, sortOrder, onSortOrderChange) { expandedSort = false }
                    SortMenuItem("Data de Importação", SortField.IMPORT_DATE, sortOrder, onSortOrderChange) { expandedSort = false }
                    SortMenuItem("Mais Tocadas", SortField.MOST_PLAYED, sortOrder, onSortOrderChange) { expandedSort = false }
                    
                    HorizontalDivider(color = HiFiColors.Divider)
                    
                    DropdownMenuItem(
                        text = { Text(if (sortOrder.direction == SortDirection.ASCENDING) "Direção: Crescente" else "Direção: Decrescente", color = HiFiColors.Ivory) },
                        onClick = {
                            val newDir = if (sortOrder.direction == SortDirection.ASCENDING) SortDirection.DESCENDING else SortDirection.ASCENDING
                            onSortOrderChange(sortOrder.copy(direction = newDir))
                            expandedSort = false
                        },
                        trailingIcon = { Icon(if (sortOrder.direction == SortDirection.ASCENDING) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward, null, tint = HiFiColors.Copper) }
                    )
                }
            }

            Spacer(Modifier.height(HiFiDimensions.ExtraLarge))
            HorizontalDivider(thickness = HiFiDimensions.BorderWidth, color = HiFiColors.Divider)

            AnimatedContent(targetState = displayedAlbums, label = "libraryFade") { targetAlbums ->
                if (songs.isEmpty() && searchQuery.isEmpty()) {
                    EmptyLibraryView()
                } else if (targetAlbums.isEmpty()) {
                    Text("Nenhuma música encontrada.", style = MaterialTheme.typography.bodyMedium, color = HiFiColors.Sand, modifier = Modifier.fillMaxWidth().padding(vertical = HiFiDimensions.ExtraLarge), textAlign = TextAlign.Center)
                } else {
                    LazyColumn(modifier = Modifier.height(HiFiDimensions.LibraryListHeight)) {
                        targetAlbums.forEach { album ->
                            val albumId = "${album.name}-${album.artist}"
                            val isExpanded = expandedAlbums.contains(albumId)
                            item(key = albumId) {
                                AlbumListItem(album = album, isExpanded = isExpanded, onClick = { expandedAlbums = if (isExpanded) expandedAlbums - albumId else expandedAlbums + albumId })
                            }
                            if (isExpanded) {
                                items(album.songs, key = { it.uri }) { song ->
                                    SongListItem(modifier = Modifier.padding(start = 32.dp), song = song, isSelected = selectedSongId == song.id, onClick = { onSongClick(song) }, onLongClick = { selectedSongToPlaylist = song; showAddToPlaylistDialog = true })
                                }
                            }
                            item { HorizontalDivider(thickness = HiFiDimensions.BorderWidth, color = HiFiColors.Divider) }
                        }
                    }
                }
            }
        }

        // --- DIALOGS ---
        if (showCreateDialog) {
            PlaylistNameDialog("Nova Playlist", newPlaylistName, { newPlaylistName = it }, { if (newPlaylistName.isNotBlank()) { onCriarPlaylist(newPlaylistName); showCreateDialog = false; newPlaylistName = "" } }, { showCreateDialog = false; newPlaylistName = "" })
        }
        if (showAddToPlaylistDialog && selectedSongToPlaylist != null) {
            AddToPlaylistDialog(playlists, { playlist ->
                val added = onAddSongToPlaylist(playlist.id, selectedSongToPlaylist!!)
                onShowSnackbar(if (added) "Música adicionada à playlist." else "Essa música já está na playlist.")
                showAddToPlaylistDialog = false; selectedSongToPlaylist = null
            }, { showAddToPlaylistDialog = false; selectedSongToPlaylist = null })
        }
        if (showManagePlaylistDialog && selectedPlaylistToManage != null) {
            ManagePlaylistDialog(selectedPlaylistToManage!!, { renamedPlaylistName = selectedPlaylistToManage?.name ?: ""; showRenameDialog = true; showManagePlaylistDialog = false }, { showDeleteConfirmDialog = true; showManagePlaylistDialog = false }, { showManagePlaylistDialog = false; selectedPlaylistToManage = null })
        }
        if (showRenameDialog && selectedPlaylistToManage != null) {
            PlaylistNameDialog("Renomear Playlist", renamedPlaylistName, { renamedPlaylistName = it }, { if (renamedPlaylistName.isNotBlank()) { onRenomearPlaylist(selectedPlaylistToManage!!.id, renamedPlaylistName); showRenameDialog = false; selectedPlaylistToManage = null } }, { showRenameDialog = false; selectedPlaylistToManage = null })
        }
        if (showDeleteConfirmDialog && selectedPlaylistToManage != null) {
            DeleteConfirmDialog(selectedPlaylistToManage!!, { onRemoverPlaylist(selectedPlaylistToManage!!.id); showDeleteConfirmDialog = false; selectedPlaylistToManage = null }, { showDeleteConfirmDialog = false; selectedPlaylistToManage = null })
        }
    }
}

@Composable
fun FilterChipItem(label: String, isSelected: Boolean, onClick: () -> Unit, icon: ImageVector) {
    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, modifier = Modifier.size(18.dp)) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (isSelected) HiFiColors.Copper else HiFiColors.Espresso,
            labelColor = if (isSelected) HiFiColors.Ivory else HiFiColors.Sand,
            leadingIconContentColor = if (isSelected) HiFiColors.Ivory else HiFiColors.Copper
        ),
        border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = if (isSelected) HiFiColors.Copper else HiFiColors.CopperDark)
    )
}

@Composable
fun SectionHeader(title: String) {
    Text(text = title, style = MaterialTheme.typography.labelSmall, color = HiFiColors.Sand, letterSpacing = 2.sp, modifier = Modifier.padding(top = HiFiDimensions.Large))
    Spacer(Modifier.height(HiFiDimensions.Medium))
}

@Composable
fun SectionItem(title: String, subtitle: String, icon: ImageVector, isExpanded: Boolean, onToggle: () -> Unit, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = HiFiDimensions.Medium)) {
        Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle).padding(vertical = HiFiDimensions.Small), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = HiFiColors.Copper, modifier = Modifier.padding(end = HiFiDimensions.Medium))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = HiFiColors.Ivory)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = HiFiColors.Sand)
            }
            Text(if (isExpanded) "▼" else "►", color = HiFiColors.Copper, style = MaterialTheme.typography.bodyLarge)
        }
        if (isExpanded) content()
        HorizontalDivider(thickness = HiFiDimensions.BorderWidth, color = HiFiColors.Divider)
    }
}

@Composable
fun SortMenuItem(label: String, field: SortField, current: SortOrder, onSelect: (SortOrder) -> Unit, onDismiss: () -> Unit) {
    DropdownMenuItem(
        text = { Text(label, color = if (current.field == field) HiFiColors.Copper else HiFiColors.Ivory) },
        onClick = { onSelect(current.copy(field = field)); onDismiss() },
        trailingIcon = { if (current.field == field) Icon(Icons.Default.Add, null, tint = HiFiColors.Copper, modifier = Modifier.size(14.dp)) else null }
    )
}

fun getSortLabel(order: SortOrder): String {
    val field = when (order.field) {
        SortField.NAME -> "Música"
        SortField.ARTIST -> "Artista"
        SortField.ALBUM -> "Álbum"
        SortField.IMPORT_DATE -> "Importação"
        SortField.MOST_PLAYED -> "Mais Tocadas"
    }
    val dir = if (order.direction == SortDirection.ASCENDING) "↑" else "↓"
    return "$field $dir"
}

@Composable
fun EmptyLibraryView() {
    Column(Modifier.fillMaxWidth().padding(vertical = HiFiDimensions.ExtraLarge), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.MusicNote, null, tint = HiFiColors.Copper.copy(0.5f), modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(HiFiDimensions.Medium))
        Text("Sua biblioteca está vazia.", style = MaterialTheme.typography.titleMedium, color = HiFiColors.Ivory, textAlign = TextAlign.Center)
        Spacer(Modifier.height(HiFiDimensions.Small))
        Text("Importe álbuns usando os botões acima.", style = MaterialTheme.typography.bodyMedium, color = HiFiColors.Sand, textAlign = TextAlign.Center)
    }
}

@Composable
fun PlaylistNameDialog(title: String, value: String, onValueChange: (String) -> Unit, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, containerColor = HiFiColors.Espresso, title = { Text(title, color = HiFiColors.Ivory) }, text = {
        OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text("Nome", color = HiFiColors.Sand) }, singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = HiFiColors.Ivory, unfocusedTextColor = HiFiColors.Ivory, cursorColor = HiFiColors.Copper, focusedBorderColor = HiFiColors.Copper, unfocusedBorderColor = HiFiColors.CopperDark))
    }, confirmButton = { TextButton(onClick = onConfirm) { Text("OK", color = HiFiColors.Copper) } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = HiFiColors.Sand) } })
}

@Composable
fun AddToPlaylistDialog(playlists: List<Playlist>, onPlaylistSelect: (Playlist) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, containerColor = HiFiColors.Espresso, title = { Text("Adicionar à Playlist", color = HiFiColors.Ivory) }, text = {
        Column {
            if (playlists.isEmpty()) Text("Nenhuma playlist criada.", color = HiFiColors.Sand)
            else playlists.forEach { playlist ->
                Row(Modifier.fillMaxWidth().clickable { onPlaylistSelect(playlist) }.padding(vertical = HiFiDimensions.Small), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PlaylistPlay, null, tint = HiFiColors.Copper, modifier = Modifier.padding(end = HiFiDimensions.Medium))
                    Column {
                        Text(playlist.name, style = MaterialTheme.typography.titleMedium, color = HiFiColors.Ivory)
                        Text("${playlist.songs.size} músicas", style = MaterialTheme.typography.bodySmall, color = HiFiColors.Sand)
                    }
                }
                HorizontalDivider(thickness = 0.5.dp, color = HiFiColors.Divider)
            }
        }
    }, confirmButton = { TextButton(onClick = onDismiss) { Text("Fechar", color = HiFiColors.Sand) } })
}

@Composable
fun ManagePlaylistDialog(playlist: Playlist, onRename: () -> Unit, onDelete: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, containerColor = HiFiColors.Espresso, title = { Text("Gerenciar Playlist", color = HiFiColors.Ivory) }, text = {
        Column {
            TextButton(onClick = onRename, modifier = Modifier.fillMaxWidth()) { Text("Renomear Playlist", color = HiFiColors.Ivory, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth()) }
            TextButton(onClick = onDelete, modifier = Modifier.fillMaxWidth()) { Text("Excluir Playlist", color = Color.Red, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth()) }
        }
    }, confirmButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = HiFiColors.Sand) } })
}

@Composable
fun DeleteConfirmDialog(playlist: Playlist, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, containerColor = HiFiColors.Espresso, title = { Text("Excluir Playlist", color = HiFiColors.Ivory) }, text = { Text("Tem certeza que deseja excluir a playlist \"${playlist.name}\"?", color = HiFiColors.Sand) }, confirmButton = { TextButton(onClick = onConfirm) { Text("Excluir", color = Color.Red) } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = HiFiColors.Sand) } })
}

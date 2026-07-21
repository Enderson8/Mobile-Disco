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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobiledisco.data.Playlist
import com.example.mobiledisco.data.Song
import com.example.mobiledisco.data.toAlbums
import com.example.mobiledisco.ui.state.SortOption
import com.example.mobiledisco.ui.theme.HiFiColors
import com.example.mobiledisco.ui.theme.HiFiDimensions

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun LibraryPanel(
    songs: List<Song>,
    playlists: List<Playlist>,
    favorites: Set<String>,
    history: List<Song>,
    mostPlayed: List<Pair<Song, Int>>, // Adicionado
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedSongId: Long?,
    isEditingPlaylist: Long? = null,
    onSongClick: (Song) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onCriarPlaylist: (String) -> Unit,
    onRenomearPlaylist: (Long, String) -> Unit,
    onRemoverPlaylist: (Long) -> Unit,
    onAddSongToPlaylist: (Long, Song) -> Boolean,
    onConcluirEdicao: () -> Unit,
    onOpenStatistics: () -> Unit,
    onOpenSettings: () -> Unit, // Adicionado
    onShowSnackbar: (String) -> Unit
) {
    var sortOption by remember { mutableStateOf(SortOption.NAME) }
    var expanded by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }
    
    var showAddToPlaylistDialog by remember { mutableStateOf(false) }
    var selectedSongToPlaylist by remember { mutableStateOf<Song?>(null) }

    var showManagePlaylistDialog by remember { mutableStateOf(false) }
    var selectedPlaylistToManage by remember { mutableStateOf<Playlist?>(null) }
    
    var showRenameDialog by remember { mutableStateOf(false) }
    var renamedPlaylistName by remember { mutableStateOf("") }
    
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    
    // Estado para controlar quais álbuns estão expandidos (pelo nome + artista)
    var expandedAlbums by remember { mutableStateOf(setOf<String>()) }
    
    // Estado para expandir seções
    var expandedFavorites by remember { mutableStateOf(false) }
    var expandedHistory by remember { mutableStateOf(false) }
    var expandedMostPlayed by remember { mutableStateOf(false) }

    val favoriteSongs = remember(songs, favorites) {
        songs.filter { favorites.contains(it.uri) }
    }

    val displayedAlbums = remember(songs, sortOption) {
        val albums = songs.toAlbums()
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
        // --- PESQUISA GLOBAL (F6.1) ---
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
                unfocusedBorderColor = HiFiColors.CopperDark,
                focusedLabelColor = HiFiColors.Copper,
                unfocusedLabelColor = HiFiColors.Sand,
                focusedPlaceholderColor = HiFiColors.Sand,
                unfocusedPlaceholderColor = HiFiColors.SoftBrown
            )
        )

        Spacer(modifier = Modifier.height(HiFiDimensions.Medium))

        // --- CABEÇALHO DE EDIÇÃO (Modo F3.6) ---
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

        // --- SEÇÃO ESTATÍSTICAS (F6.4) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HiFiDimensions.Medium)
                .clickable { onOpenStatistics() }
                .padding(vertical = HiFiDimensions.Medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.BarChart,
                contentDescription = null,
                tint = HiFiColors.Copper,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "📊 ESTATÍSTICAS",
                style = MaterialTheme.typography.labelSmall,
                color = HiFiColors.Ivory,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(start = HiFiDimensions.Medium)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "▶",
                color = HiFiColors.Copper,
                style = MaterialTheme.typography.bodySmall
            )
        }

        HorizontalDivider(thickness = HiFiDimensions.BorderWidth, color = HiFiColors.Divider)

        // --- SEÇÃO CONFIGURAÇÕES (F7.0) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HiFiDimensions.Medium)
                .clickable { onOpenSettings() }
                .padding(vertical = HiFiDimensions.Medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = HiFiColors.Copper,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "⚙ CONFIGURAÇÕES",
                style = MaterialTheme.typography.labelSmall,
                color = HiFiColors.Ivory,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(start = HiFiDimensions.Medium)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "▶",
                color = HiFiColors.Copper,
                style = MaterialTheme.typography.bodySmall
            )
        }

        HorizontalDivider(thickness = HiFiDimensions.BorderWidth, color = HiFiColors.Divider)
        }

        // --- SEÇÃO HISTÓRICO ---
        if (history.isNotEmpty()) {
            Text(
                text = "🕒 ÚLTIMAS REPRODUZIDAS",
                style = MaterialTheme.typography.labelSmall,
                color = HiFiColors.Sand,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = HiFiDimensions.Large)
            )

            Spacer(modifier = Modifier.height(HiFiDimensions.Medium))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HiFiDimensions.Medium)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedHistory = !expandedHistory }
                        .padding(vertical = HiFiDimensions.Small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = HiFiColors.Copper,
                        modifier = Modifier.padding(end = HiFiDimensions.Medium)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Histórico",
                            style = MaterialTheme.typography.titleMedium,
                            color = HiFiColors.Ivory
                        )
                        Text(
                            text = "${history.size} músicas",
                            style = MaterialTheme.typography.bodySmall,
                            color = HiFiColors.Sand
                        )
                    }
                    Text(
                        text = if (expandedHistory) "▼" else "►",
                        color = HiFiColors.Copper,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                if (expandedHistory) {
                    history.forEach { song ->
                        SongListItem(
                            song = song,
                            isSelected = selectedSongId == song.id,
                            onClick = { onSongClick(song) },
                            modifier = Modifier.padding(start = 32.dp)
                        )
                    }
                }
                
                HorizontalDivider(thickness = HiFiDimensions.BorderWidth, color = HiFiColors.Divider)
            }
        }

        // --- SEÇÃO FAVORITOS ---
        if (favoriteSongs.isNotEmpty()) {
            Text(
                text = "⭐ FAVORITOS",
                style = MaterialTheme.typography.labelSmall,
                color = HiFiColors.Sand,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = HiFiDimensions.Large)
            )

            Spacer(modifier = Modifier.height(HiFiDimensions.Medium))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HiFiDimensions.Medium)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedFavorites = !expandedFavorites }
                        .padding(vertical = HiFiDimensions.Small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = HiFiColors.Copper,
                        modifier = Modifier.padding(end = HiFiDimensions.Medium)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Músicas Favoritas",
                            style = MaterialTheme.typography.titleMedium,
                            color = HiFiColors.Ivory
                        )
                        Text(
                            text = "${favoriteSongs.size} músicas",
                            style = MaterialTheme.typography.bodySmall,
                            color = HiFiColors.Sand
                        )
                    }
                    Text(
                        text = if (expandedFavorites) "▼" else "►",
                        color = HiFiColors.Copper,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                if (expandedFavorites) {
                    favoriteSongs.forEach { song ->
                        SongListItem(
                            song = song,
                            isSelected = selectedSongId == song.id,
                            onClick = { onSongClick(song) },
                            modifier = Modifier.padding(start = 32.dp)
                        )
                    }
                }
                
                HorizontalDivider(thickness = HiFiDimensions.BorderWidth, color = HiFiColors.Divider)
            }
        }

        // --- SEÇÃO MAIS TOCADAS ---
        if (mostPlayed.isNotEmpty()) {
            Text(
                text = "🔥 MAIS TOCADAS",
                style = MaterialTheme.typography.labelSmall,
                color = HiFiColors.Sand,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = HiFiDimensions.Large)
            )

            Spacer(modifier = Modifier.height(HiFiDimensions.Medium))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HiFiDimensions.Medium)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedMostPlayed = !expandedMostPlayed }
                        .padding(vertical = HiFiDimensions.Small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = HiFiColors.Copper,
                        modifier = Modifier.padding(end = HiFiDimensions.Medium)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mais Tocadas",
                            style = MaterialTheme.typography.titleMedium,
                            color = HiFiColors.Ivory
                        )
                        Text(
                            text = "${mostPlayed.size} faixas populares",
                            style = MaterialTheme.typography.bodySmall,
                            color = HiFiColors.Sand
                        )
                    }
                    Text(
                        text = if (expandedMostPlayed) "▼" else "►",
                        color = HiFiColors.Copper,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                if (expandedMostPlayed) {
                    mostPlayed.forEach { (song, count) ->
                        SongListItem(
                            song = song,
                            isSelected = selectedSongId == song.id,
                            onClick = { onSongClick(song) },
                            modifier = Modifier.padding(start = 32.dp),
                            label = "$count"
                        )
                    }
                }
                
                HorizontalDivider(thickness = HiFiDimensions.BorderWidth, color = HiFiColors.Divider)
            }
        }

        // --- SEÇÃO PLAYLISTS ---
        Text(
            text = "PLAYLISTS",
            style = MaterialTheme.typography.labelSmall,
            color = HiFiColors.Sand,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(top = HiFiDimensions.Large)
        )

        Spacer(modifier = Modifier.height(HiFiDimensions.Medium))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HiFiDimensions.Medium)
        ) {
            playlists.forEach { playlist ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { onPlaylistClick(playlist) },
                            onLongClick = {
                                selectedPlaylistToManage = playlist
                                showManagePlaylistDialog = true
                            }
                        )
                        .padding(vertical = HiFiDimensions.Small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlaylistPlay,
                        contentDescription = null,
                        tint = HiFiColors.Copper,
                        modifier = Modifier.padding(end = HiFiDimensions.Medium)
                    )
                    Column {
                        Text(
                            text = playlist.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = HiFiColors.Ivory
                        )
                        Text(
                            text = "${playlist.songs.size} músicas",
                            style = MaterialTheme.typography.bodySmall,
                            color = HiFiColors.Sand
                        )
                    }
                }
                HorizontalDivider(thickness = HiFiDimensions.BorderWidth, color = HiFiColors.Divider)
            }

            Spacer(modifier = Modifier.height(HiFiDimensions.Medium))

            Button(
                onClick = { showCreateDialog = true },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HiFiColors.Walnut700,
                    contentColor = HiFiColors.Ivory
                ),
                shape = RoundedCornerShape(HiFiDimensions.Small)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(HiFiDimensions.Small))
                Text("NOVA PLAYLIST", letterSpacing = 1.sp)
            }
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showCreateDialog = false
                    newPlaylistName = ""
                },
                containerColor = HiFiColors.Espresso,
                title = { 
                    Text("Nova Playlist", color = HiFiColors.Ivory) 
                },
                text = {
                    OutlinedTextField(
                        value = newPlaylistName,
                        onValueChange = { newPlaylistName = it },
                        label = { Text("Nome da playlist", color = HiFiColors.Sand) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = HiFiColors.Ivory,
                            unfocusedTextColor = HiFiColors.Ivory,
                            cursorColor = HiFiColors.Copper,
                            focusedBorderColor = HiFiColors.Copper,
                            unfocusedBorderColor = HiFiColors.CopperDark
                        )
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newPlaylistName.isNotBlank()) {
                                onCriarPlaylist(newPlaylistName)
                                showCreateDialog = false
                                newPlaylistName = ""
                            }
                        }
                    ) {
                        Text("Criar", color = HiFiColors.Copper)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { 
                            showCreateDialog = false
                            newPlaylistName = ""
                        }
                    ) {
                        Text("Cancelar", color = HiFiColors.Sand)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(HiFiDimensions.ExtraLarge))
        HorizontalDivider(thickness = HiFiDimensions.BorderWidth, color = HiFiColors.Divider)
        Spacer(modifier = Modifier.height(HiFiDimensions.Large))

        // --- SEÇÃO ÁLBUNS (BIBLIOTECA ORIGINAL) ---
        Text(
            text = "MUSIC LIBRARY",
            style = MaterialTheme.typography.labelSmall,
            color = HiFiColors.Sand,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(HiFiDimensions.Tiny))

        // 2. Contador (Informação secundária)
        Text(
            text = if (searchQuery.isEmpty())
                "${songs.size} músicas"
            else
                "${displayedAlbums.size} resultados",
            style = MaterialTheme.typography.bodySmall,
            color = HiFiColors.Sand
        )

        Spacer(modifier = Modifier.height(HiFiDimensions.Large))

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
            if (songs.isEmpty() && searchQuery.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = HiFiDimensions.ExtraLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = HiFiColors.Copper.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(HiFiDimensions.Medium))
                    Text(
                        text = "Sua biblioteca está vazia.",
                        style = MaterialTheme.typography.titleMedium,
                        color = HiFiColors.Ivory,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(HiFiDimensions.Small))
                    Text(
                        text = "Importe álbuns usando os botões acima.",
                        style = MaterialTheme.typography.bodyMedium,
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
                                    },
                                    onLongClick = {
                                        selectedSongToPlaylist = song
                                        showAddToPlaylistDialog = true
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

        if (showAddToPlaylistDialog && selectedSongToPlaylist != null) {
            AlertDialog(
                onDismissRequest = { 
                    showAddToPlaylistDialog = false
                    selectedSongToPlaylist = null
                },
                containerColor = HiFiColors.Espresso,
                title = { 
                    Text("Adicionar à Playlist", color = HiFiColors.Ivory) 
                },
                text = {
                    Column {
                        if (playlists.isEmpty()) {
                            Text("Nenhuma playlist criada.", color = HiFiColors.Sand)
                        } else {
                            playlists.forEach { playlist ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val added = onAddSongToPlaylist(playlist.id, selectedSongToPlaylist!!)
                                            if (added) {
                                                onShowSnackbar("Música adicionada à playlist.")
                                            } else {
                                                onShowSnackbar("Essa música já está na playlist.")
                                            }
                                            showAddToPlaylistDialog = false
                                            selectedSongToPlaylist = null
                                        }
                                        .padding(vertical = HiFiDimensions.Small),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlaylistPlay,
                                        contentDescription = null,
                                        tint = HiFiColors.Copper,
                                        modifier = Modifier.padding(end = HiFiDimensions.Medium)
                                    )
                                    Column {
                                        Text(
                                            text = playlist.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = HiFiColors.Ivory
                                        )
                                        Text(
                                            text = "${playlist.songs.size} músicas",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = HiFiColors.Sand
                                        )
                                    }
                                }
                                HorizontalDivider(thickness = 0.5.dp, color = HiFiColors.Divider)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { 
                        showAddToPlaylistDialog = false
                        selectedSongToPlaylist = null
                    }) {
                        Text("Fechar", color = HiFiColors.Sand)
                    }
                }
            )
        }

        if (showManagePlaylistDialog && selectedPlaylistToManage != null) {
            AlertDialog(
                onDismissRequest = { 
                    showManagePlaylistDialog = false
                    selectedPlaylistToManage = null
                },
                containerColor = HiFiColors.Espresso,
                title = { Text("Gerenciar Playlist", color = HiFiColors.Ivory) },
                text = {
                    Column {
                        TextButton(
                            onClick = {
                                renamedPlaylistName = selectedPlaylistToManage?.name ?: ""
                                showRenameDialog = true
                                showManagePlaylistDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Renomear Playlist", color = HiFiColors.Ivory, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
                        }
                        TextButton(
                            onClick = {
                                showDeleteConfirmDialog = true
                                showManagePlaylistDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Excluir Playlist", color = Color.Red, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { 
                        showManagePlaylistDialog = false
                        selectedPlaylistToManage = null
                    }) {
                        Text("Cancelar", color = HiFiColors.Sand)
                    }
                }
            )
        }

        if (showRenameDialog && selectedPlaylistToManage != null) {
            AlertDialog(
                onDismissRequest = { 
                    showRenameDialog = false
                    selectedPlaylistToManage = null
                },
                containerColor = HiFiColors.Espresso,
                title = { Text("Renomear Playlist", color = HiFiColors.Ivory) },
                text = {
                    OutlinedTextField(
                        value = renamedPlaylistName,
                        onValueChange = { renamedPlaylistName = it },
                        label = { Text("Novo nome", color = HiFiColors.Sand) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = HiFiColors.Ivory,
                            unfocusedTextColor = HiFiColors.Ivory,
                            cursorColor = HiFiColors.Copper,
                            focusedBorderColor = HiFiColors.Copper,
                            unfocusedBorderColor = HiFiColors.CopperDark
                        )
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (renamedPlaylistName.isNotBlank()) {
                            onRenomearPlaylist(selectedPlaylistToManage!!.id, renamedPlaylistName)
                            showRenameDialog = false
                            selectedPlaylistToManage = null
                        }
                    }) {
                        Text("Salvar", color = HiFiColors.Copper)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showRenameDialog = false
                        selectedPlaylistToManage = null
                    }) {
                        Text("Cancelar", color = HiFiColors.Sand)
                    }
                }
            )
        }

        if (showDeleteConfirmDialog && selectedPlaylistToManage != null) {
            AlertDialog(
                onDismissRequest = { 
                    showDeleteConfirmDialog = false
                    selectedPlaylistToManage = null
                },
                containerColor = HiFiColors.Espresso,
                title = { Text("Excluir Playlist", color = HiFiColors.Ivory) },
                text = { Text("Tem certeza que deseja excluir a playlist \"${selectedPlaylistToManage?.name}\"?", color = HiFiColors.Sand) },
                confirmButton = {
                    TextButton(onClick = {
                        onRemoverPlaylist(selectedPlaylistToManage!!.id)
                        showDeleteConfirmDialog = false
                        selectedPlaylistToManage = null
                    }) {
                        Text("Excluir", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteConfirmDialog = false
                        selectedPlaylistToManage = null
                    }) {
                        Text("Cancelar", color = HiFiColors.Sand)
                    }
                }
            )
        }
    }
}

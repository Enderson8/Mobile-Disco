package com.example.mobiledisco.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mobiledisco.data.Playlist
import com.example.mobiledisco.data.Song
import com.example.mobiledisco.ui.components.SongListItem
import com.example.mobiledisco.ui.theme.HiFiColors
import com.example.mobiledisco.ui.theme.HiFiDimensions

@Composable
fun PlaylistScreen(
    playlist: Playlist,
    selectedSongId: Long?,
    onSongClick: (Playlist, Song) -> Unit,
    onRemoveSong: (Song) -> Unit,
    onAddSongsClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showRemoveDialog by remember { mutableStateOf(false) }
    var songToRemove by remember { mutableStateOf<Song?>(null) }

    BackHandler {
        onBack()
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = HiFiColors.Walnut900
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HiFiDimensions.Medium, vertical = HiFiDimensions.Small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = HiFiColors.Ivory
                    )
                }
                
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.PlaylistPlay,
                    contentDescription = null,
                    tint = HiFiColors.Copper,
                    modifier = Modifier.size(32.dp).padding(horizontal = 4.dp)
                )

                Column(modifier = Modifier.padding(start = 8.dp).weight(1f)) {
                    Text(
                        text = playlist.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = HiFiColors.Ivory
                    )
                    Text(
                        text = "${playlist.songs.size} músicas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HiFiColors.Sand
                    )
                }

                IconButton(onClick = onAddSongsClick) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar músicas",
                        tint = HiFiColors.Copper
                    )
                }
            }

            Spacer(modifier = Modifier.height(HiFiDimensions.Medium))
            
            HorizontalDivider(
                thickness = HiFiDimensions.BorderWidth,
                color = HiFiColors.Divider
            )

            if (playlist.songs.isEmpty()) {
                // Estado Vazio
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(HiFiDimensions.Large),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = HiFiColors.Copper.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(HiFiDimensions.Medium))
                    
                    Text(
                        text = "Esta playlist está vazia.",
                        style = MaterialTheme.typography.titleMedium,
                        color = HiFiColors.Ivory,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "Adicione músicas para começar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HiFiColors.Sand,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(HiFiDimensions.ExtraLarge))
                    
                    Button(
                        onClick = { onAddSongsClick() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HiFiColors.Walnut700,
                            contentColor = HiFiColors.Ivory
                        ),
                        shape = RoundedCornerShape(HiFiDimensions.Small)
                    ) {
                        Text("ADICIONAR MÚSICAS")
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(playlist.songs, key = { it.uri }) { song ->
                        SongListItem(
                            song = song,
                            isSelected = selectedSongId == song.id,
                            onClick = { onSongClick(playlist, song) },
                            onLongClick = {
                                songToRemove = song
                                showRemoveDialog = true
                            }
                        )
                        HorizontalDivider(
                            thickness = HiFiDimensions.BorderWidth,
                            color = HiFiColors.Divider
                        )
                    }
                }
            }
        }

        if (showRemoveDialog && songToRemove != null) {
            AlertDialog(
                onDismissRequest = { 
                    showRemoveDialog = false
                    songToRemove = null
                },
                containerColor = HiFiColors.Espresso,
                title = { Text("Remover da Playlist", color = HiFiColors.Ivory) },
                text = { Text("Deseja remover \"${songToRemove?.name}\" desta playlist?", color = HiFiColors.Sand) },
                confirmButton = {
                    TextButton(onClick = {
                        onRemoveSong(songToRemove!!)
                        showRemoveDialog = false
                        songToRemove = null
                    }) {
                        Text("Remover", color = HiFiColors.Copper)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showRemoveDialog = false
                        songToRemove = null
                    }) {
                        Text("Cancelar", color = HiFiColors.Sand)
                    }
                }
            )
        }
    }
}

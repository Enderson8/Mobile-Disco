package com.example.mobiledisco.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobiledisco.ui.components.HiFiCard
import com.example.mobiledisco.ui.theme.HiFiColors
import com.example.mobiledisco.ui.theme.HiFiDimensions
import com.example.mobiledisco.viewmodel.MusicStatistics

@Composable
fun StatisticsScreen(
    stats: MusicStatistics,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(HiFiDimensions.Medium),
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
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    tint = HiFiColors.Copper,
                    modifier = Modifier.size(32.dp)
                )

                Text(
                    text = "ESTATÍSTICAS",
                    style = MaterialTheme.typography.headlineSmall,
                    color = HiFiColors.Ivory,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(HiFiDimensions.Medium))

            HiFiCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HiFiDimensions.Medium)
            ) {
                Column(modifier = Modifier.padding(HiFiDimensions.Medium)) {
                    StatRow("Músicas", stats.totalSongs.toString())
                    StatRow("Álbuns", stats.totalAlbums.toString())
                    StatRow("Playlists", stats.totalPlaylists.toString())
                    StatRow("Favoritos", stats.totalFavorites.toString())
                    StatRow("No Histórico", stats.totalHistory.toString())
                    StatRow("Total de Plays", stats.totalPlays.toString())
                }
            }

            Spacer(modifier = Modifier.height(HiFiDimensions.Large))

            Text(
                text = "DESTAQUES",
                style = MaterialTheme.typography.labelSmall,
                color = HiFiColors.Sand,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(horizontal = HiFiDimensions.Medium)
            )

            Spacer(modifier = Modifier.height(HiFiDimensions.Small))

            HiFiCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HiFiDimensions.Medium)
            ) {
                Column(modifier = Modifier.padding(HiFiDimensions.Medium)) {
                    HighlightStat("Música mais ouvida", stats.mostPlayedSong?.let { "${it.first.name} (${it.second} plays)" } ?: "-")
                    HighlightStat("Artista favorito", stats.mostPlayedArtist?.let { "${it.first} (${it.second} plays)" } ?: "-")
                    HighlightStat("Álbum favorito", stats.mostPlayedAlbum?.let { "${it.first} (${it.second} plays)" } ?: "-")
                }
            }
            
            Spacer(modifier = Modifier.height(HiFiDimensions.ExtraLarge))
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = HiFiColors.Sand, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, color = HiFiColors.Ivory, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun HighlightStat(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, color = HiFiColors.Sand, style = MaterialTheme.typography.labelSmall)
        Text(text = value, color = HiFiColors.Ivory, style = MaterialTheme.typography.titleMedium)
    }
}

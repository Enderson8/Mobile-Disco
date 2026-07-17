package com.example.mobiledisco.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobiledisco.player.PlaybackStatus
import com.example.mobiledisco.player.PlayerEvent
import com.example.mobiledisco.player.PlayerUiState
import com.example.mobiledisco.ui.components.AlbumCover
import com.example.mobiledisco.ui.components.MusicInfo
import com.example.mobiledisco.ui.components.PlayerControls
import com.example.mobiledisco.ui.components.TimeSlider
import com.example.mobiledisco.ui.theme.HiFiColors
import com.example.mobiledisco.ui.theme.HiFiDimensions

@Composable
fun NowPlayingScreen(
    state: PlayerUiState,
    onEvent: (PlayerEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = HiFiColors.Walnut900
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(HiFiDimensions.Large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "MOBILE DISCO",
                style = MaterialTheme.typography.headlineSmall,
                color = HiFiColors.Sand,
                letterSpacing = 4.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(HiFiDimensions.ExtraLarge))

            // Capa do Álbum (com pulso animado quando toca)
            AlbumCover(
                musica = state.musica,
                isPlaying = state.playbackStatus == PlaybackStatus.PLAYING
            )

            Spacer(modifier = Modifier.height(HiFiDimensions.Large))

            MusicInfo(musica = state.musica)
            
            state.musica?.let { song ->
                IconButton(onClick = { onEvent(PlayerEvent.ToggleFavorite(song)) }) {
                    Icon(
                        imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (state.isFavorite) HiFiColors.Copper else HiFiColors.Sand,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(HiFiDimensions.Medium))

            TimeSlider(
                currentPosition = state.currentPosition,
                duration = state.duration,
                onSeek = { onEvent(PlayerEvent.Seek(it)) }
            )

            Spacer(modifier = Modifier.height(HiFiDimensions.Large))

            PlayerControls(
                state = state,
                onEvent = onEvent
            )
        }
    }
}

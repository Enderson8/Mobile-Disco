package com.example.mobiledisco.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobiledisco.player.PlayerEvent
import com.example.mobiledisco.player.PlayerUiState
import com.example.mobiledisco.ui.theme.HiFiColors
import com.example.mobiledisco.ui.theme.HiFiDimensions

@Composable
fun PlayerPanel(
    state: PlayerUiState,
    onEvent: (PlayerEvent) -> Unit,
    onCoverClick: () -> Unit = {}
) {
    HiFiCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(HiFiDimensions.Medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PowerIndicator(
                status = state.playbackStatus
            )

            state.musica?.let { song ->
                IconButton(onClick = { onEvent(PlayerEvent.ToggleFavorite(song)) }) {
                    Icon(
                        imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (state.isFavorite) HiFiColors.Copper else HiFiColors.Sand,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(HiFiDimensions.Large))

        AlbumCover(
            musica = state.musica,
            onClick = onCoverClick
        )

        Spacer(modifier = Modifier.height(HiFiDimensions.Large))

        MusicInfo(
            musica = state.musica
        )

        Spacer(modifier = Modifier.height(HiFiDimensions.ExtraLarge))

        HorizontalDivider(
            thickness = HiFiDimensions.BorderWidth,
            color = HiFiColors.Divider
        )

        Spacer(modifier = Modifier.height(HiFiDimensions.Medium))

        TimeSlider(
            currentPosition = state.currentPosition,
            duration = state.duration,
            onSeek = {
                onEvent(PlayerEvent.Seek(it))
            }
        )

        Spacer(modifier = Modifier.height(HiFiDimensions.Medium))

        HorizontalDivider(
            thickness = HiFiDimensions.BorderWidth,
            color = HiFiColors.Divider
        )

        Spacer(modifier = Modifier.height(HiFiDimensions.ExtraLarge))

        PlayerControls(
            state = state,
            onEvent = onEvent
        )
    }
}

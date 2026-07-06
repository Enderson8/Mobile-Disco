package com.example.mobiledisco.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mobiledisco.player.PlayerEvent
import com.example.mobiledisco.player.PlayerUiState
import com.example.mobiledisco.ui.theme.HiFiDimensions

@Composable
fun PlayerPanel(
    state: PlayerUiState,
    onEvent: (PlayerEvent) -> Unit
) {
    HiFiCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(HiFiDimensions.Medium)
    ) {
        LedIndicator(
            status = state.playbackStatus
        )

        Spacer(modifier = Modifier.height(HiFiDimensions.Medium))

        AlbumCover(
            musica = state.musica
        )

        Spacer(modifier = Modifier.height(HiFiDimensions.Medium))

        MusicInfo(
            musica = state.musica
        )

        Spacer(modifier = Modifier.height(HiFiDimensions.CardPadding))

        TimeSlider(
            currentPosition = state.currentPosition,
            duration = state.duration,
            onSeek = {
                onEvent(PlayerEvent.Seek(it))
            }
        )

        Spacer(modifier = Modifier.height(HiFiDimensions.CardPadding))

        PlayerControls(
            status = state.playbackStatus,
            onEvent = onEvent
        )
    }
}

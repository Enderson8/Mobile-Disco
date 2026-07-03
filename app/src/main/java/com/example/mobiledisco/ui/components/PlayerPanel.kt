package com.example.mobiledisco.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobiledisco.player.PlayerEvent
import com.example.mobiledisco.player.PlayerUiState

@Composable
fun PlayerPanel(
    state: PlayerUiState,
    onEvent: (PlayerEvent) -> Unit
) {
    HiFiCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        AlbumCover(
            musica = state.musica
        )

        Spacer(modifier = Modifier.height(16.dp))

        MusicInfo(
            musica = state.musica
        )

        Spacer(modifier = Modifier.height(20.dp))

        TimeSlider(
            currentPosition = state.currentPosition,
            duration = state.duration,
            onEvent = onEvent
        )

        Spacer(modifier = Modifier.height(20.dp))

        PlayerControls(
            isPlaying = state.isPlaying,
            onEvent = onEvent
        )
    }
}

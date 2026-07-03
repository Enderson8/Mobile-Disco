package com.example.mobiledisco.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.mobiledisco.player.PlaybackStatus
import com.example.mobiledisco.player.PlayerEvent

@Composable
fun PlayerControls(
    status: PlaybackStatus,
    onEvent: (PlayerEvent) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            onEvent(PlayerEvent.Previous)
        }) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "Anterior"
            )
        }

        IconButton(onClick = {
            onEvent(PlayerEvent.PlayPause)
        }) {
            Icon(
                imageVector = if (status == PlaybackStatus.PLAYING) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (status == PlaybackStatus.PLAYING) "Pausar" else "Reproduzir"
            )
        }

        IconButton(onClick = {
            onEvent(PlayerEvent.Stop)
        }) {
            Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = "Parar"
            )
        }

        IconButton(onClick = {
            onEvent(PlayerEvent.Next)
        }) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Próximo"
            )
        }
    }
}

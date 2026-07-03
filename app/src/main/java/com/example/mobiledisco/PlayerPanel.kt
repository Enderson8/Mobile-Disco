package com.example.mobiledisco

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlayerPanel(
    state: PlayerUiState,
    onEvent: (PlayerEvent) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AlbumCover(
                musica = state.musica
            )

            Spacer(modifier = Modifier.height(16.dp))

            MusicInfo(
                musica = state.musica
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "${formatTime(state.currentPosition)} / ${formatTime(state.duration)}")

            Slider(
                value = state.currentPosition.toFloat(),
                onValueChange = { novoValor ->
                    onEvent(PlayerEvent.Seek(novoValor.toLong()))
                },
                valueRange = 0f..state.duration.toFloat().coerceAtLeast(0f),
                enabled = state.duration > 0,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

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
                        imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (state.isPlaying) "Pausar" else "Reproduzir"
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
    }
}

package com.example.mobiledisco

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
                Button(onClick = {
                    onEvent(PlayerEvent.Previous)
                }) {
                    Text("⏮")
                }

                Button(
                    onClick = {
                        onEvent(PlayerEvent.PlayPause)
                    }
                ) {
                    Text(if (state.isPlaying) "⏸" else "▶")
                }

                Button(onClick = {
                    onEvent(PlayerEvent.Stop)
                }) {
                    Text("⏹")
                }

                Button(onClick = {
                    onEvent(PlayerEvent.Next)
                }) {
                    Text("⏭")
                }
            }
        }
    }
}

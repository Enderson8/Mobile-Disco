package com.example.mobiledisco.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun TimeSlider(
    currentPosition: Long,
    duration: Long,
    onSeek: (Long) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "${formatTime(currentPosition)} / ${formatTime(duration)}")

        Slider(
            value = currentPosition.toFloat(),
            onValueChange = { novoValor ->
                onSeek(novoValor.toLong())
            },
            valueRange = 0f..duration.toFloat().coerceAtLeast(0f),
            enabled = duration > 0,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

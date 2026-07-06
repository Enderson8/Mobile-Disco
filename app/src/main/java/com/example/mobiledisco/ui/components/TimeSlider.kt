package com.example.mobiledisco.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.mobiledisco.utils.formatTime

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

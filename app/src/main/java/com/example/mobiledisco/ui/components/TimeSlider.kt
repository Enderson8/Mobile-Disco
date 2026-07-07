package com.example.mobiledisco.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.mobiledisco.ui.theme.HiFiColors
import com.example.mobiledisco.ui.theme.HiFiDimensions
import com.example.mobiledisco.utils.formatTime

@Composable
fun TimeSlider(
    currentPosition: Long,
    duration: Long,
    onSeek: (Long) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = HiFiDimensions.Small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HiFiDimensions.Small)
    ) {
        Text(
            text = formatTime(currentPosition),
            style = MaterialTheme.typography.labelMedium,
            color = HiFiColors.Sand
        )

        Slider(
            value = currentPosition.toFloat(),
            onValueChange = { novoValor ->
                onSeek(novoValor.toLong())
            },
            valueRange = 0f..duration.toFloat().coerceAtLeast(0f),
            enabled = duration > 0,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = HiFiColors.Copper,
                activeTrackColor = HiFiColors.Copper,
                inactiveTrackColor = HiFiColors.Walnut700,
                disabledThumbColor = HiFiColors.LedOff,
                disabledActiveTrackColor = HiFiColors.LedOff
            )
        )

        Text(
            text = formatTime(duration),
            style = MaterialTheme.typography.labelMedium,
            color = HiFiColors.Sand
        )
    }
}

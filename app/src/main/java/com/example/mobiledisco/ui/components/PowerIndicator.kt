package com.example.mobiledisco.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.mobiledisco.player.PlaybackStatus
import com.example.mobiledisco.ui.theme.HiFiColors
import com.example.mobiledisco.ui.theme.HiFiDimensions

@Composable
fun PowerIndicator(
    status: PlaybackStatus
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        LedIndicator(status = status)

        Spacer(modifier = Modifier.width(HiFiDimensions.Small))

        Text(
            text = "POWER",
            style = MaterialTheme.typography.labelSmall,
            color = HiFiColors.Sand
        )
    }
}

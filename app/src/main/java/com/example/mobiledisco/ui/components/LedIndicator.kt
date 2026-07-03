package com.example.mobiledisco.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import com.example.mobiledisco.player.PlaybackStatus
import com.example.mobiledisco.ui.theme.HiFiColors
import com.example.mobiledisco.ui.theme.HiFiDimensions

@Composable
fun LedIndicator(
    status: PlaybackStatus
) {
    val targetColor = when (status) {
        PlaybackStatus.PLAYING -> HiFiColors.LedGreen
        PlaybackStatus.PAUSED -> HiFiColors.LedAmber
        PlaybackStatus.STOPPED -> HiFiColors.LedOff
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(300),
        label = "ledColor"
    )

    val scale by animateFloatAsState(
        targetValue = if (status == PlaybackStatus.PLAYING) 1.2f else 1f,
        animationSpec = if (status == PlaybackStatus.PLAYING) {
            infiniteRepeatable(
                animation = tween(800),
                repeatMode = RepeatMode.Reverse
            )
        } else {
            tween(300)
        },
        label = "ledScale"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(HiFiDimensions.Small)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale
                )
                .clip(CircleShape)
                .background(animatedColor)
        )

        Spacer(
            modifier = Modifier.width(HiFiDimensions.Small)
        )

        Text(
            text = when (status) {
                PlaybackStatus.PLAYING -> "Playing"
                PlaybackStatus.PAUSED -> "Paused"
                PlaybackStatus.STOPPED -> "Stopped"
            },
            style = MaterialTheme.typography.bodySmall
        )
    }
}

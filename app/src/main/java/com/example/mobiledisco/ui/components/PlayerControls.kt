package com.example.mobiledisco.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.mobiledisco.player.PlaybackStatus
import com.example.mobiledisco.player.PlayerEvent
import com.example.mobiledisco.ui.theme.HiFiColors

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
        HiFiControlButton(
            icon = Icons.Default.SkipPrevious,
            contentDescription = "Anterior",
            onClick = { onEvent(PlayerEvent.Previous) },
            size = 42
        )

        HiFiControlButton(
            icon = if (status == PlaybackStatus.PLAYING) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (status == PlaybackStatus.PLAYING) "Pausar" else "Reproduzir",
            onClick = { onEvent(PlayerEvent.PlayPause) },
            size = 64
        )

        HiFiControlButton(
            icon = Icons.Default.Stop,
            contentDescription = "Parar",
            onClick = { onEvent(PlayerEvent.Stop) },
            size = 42
        )

        HiFiControlButton(
            icon = Icons.Default.SkipNext,
            contentDescription = "Próximo",
            onClick = { onEvent(PlayerEvent.Next) },
            size = 42
        )
    }
}

@Composable
fun HiFiControlButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    size: Int
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(size.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = HiFiColors.Copper,
            contentColor = HiFiColors.Ivory
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 1.dp
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size((size * 0.6).dp),
            tint = HiFiColors.Ivory
        )
    }
}

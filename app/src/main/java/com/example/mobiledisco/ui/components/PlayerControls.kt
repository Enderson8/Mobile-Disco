package com.example.mobiledisco.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.mobiledisco.player.PlaybackStatus
import com.example.mobiledisco.player.PlayerEvent
import com.example.mobiledisco.player.PlayerUiState
import com.example.mobiledisco.player.RepeatMode
import com.example.mobiledisco.ui.theme.HiFiColors

@Composable
fun PlayerControls(
    state: PlayerUiState,
    onEvent: (PlayerEvent) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Shuffle
        HiFiControlButton(
            icon = Icons.Default.Shuffle,
            contentDescription = "Shuffle",
            onClick = { onEvent(PlayerEvent.ToggleShuffle) },
            size = 42,
            isActive = state.isShuffleEnabled
        )

        HiFiControlButton(
            icon = Icons.Default.SkipPrevious,
            contentDescription = "Anterior",
            onClick = { onEvent(PlayerEvent.Previous) },
            size = 42
        )

        // Botão Play/Pause com Crossfade
        HiFiControlButtonLayout(
            onClick = { onEvent(PlayerEvent.PlayPause) },
            size = 64
        ) {
            Crossfade(targetState = state.playbackStatus, label = "playPauseFade") { currentStatus ->
                Icon(
                    imageVector = if (currentStatus == PlaybackStatus.PLAYING) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (currentStatus == PlaybackStatus.PLAYING) "Pausar" else "Reproduzir",
                    modifier = Modifier.size((64 * 0.6).dp),
                    tint = HiFiColors.Ivory
                )
            }
        }

        HiFiControlButton(
            icon = Icons.Default.SkipNext,
            contentDescription = "Próximo",
            onClick = { onEvent(PlayerEvent.Next) },
            size = 42
        )

        // Repeat
        val repeatIcon = when (state.repeatMode) {
            RepeatMode.ONE -> Icons.Default.RepeatOne
            else -> Icons.Default.Repeat
        }
        HiFiControlButton(
            icon = repeatIcon,
            contentDescription = "Repeat",
            onClick = { onEvent(PlayerEvent.ToggleRepeat) },
            size = 42,
            isActive = state.repeatMode != RepeatMode.NONE
        )
    }
}

@Composable
fun HiFiControlButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    size: Int,
    isActive: Boolean = false
) {
    HiFiControlButtonLayout(
        onClick = onClick, 
        size = size,
        containerColor = if (isActive) HiFiColors.CopperLight else HiFiColors.Copper
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size((size * 0.6).dp),
            tint = HiFiColors.Ivory
        )
    }
}

@Composable
fun HiFiControlButtonLayout(
    onClick: () -> Unit,
    size: Int,
    containerColor: Color = HiFiColors.Copper,
    content: @Composable () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(size.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = HiFiColors.Ivory
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 1.dp
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        content()
    }
}

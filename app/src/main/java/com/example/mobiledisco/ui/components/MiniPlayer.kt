package com.example.mobiledisco.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mobiledisco.player.PlaybackStatus
import com.example.mobiledisco.player.PlayerEvent
import com.example.mobiledisco.player.PlayerUiState
import com.example.mobiledisco.ui.theme.HiFiColors
import com.example.mobiledisco.ui.theme.HiFiDimensions

@Composable
fun MiniPlayer(
    state: PlayerUiState,
    onEvent: (PlayerEvent) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val song = state.musica ?: return

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable { onClick() },
        color = HiFiColors.Espresso,
        tonalElevation = 4.dp,
        border = BorderStroke(1.dp, HiFiColors.Divider)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = HiFiDimensions.Medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Capa
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(HiFiDimensions.ExtraSmall))
                    .border(1.dp, HiFiColors.CopperDark, RoundedCornerShape(HiFiDimensions.ExtraSmall))
                    .background(HiFiColors.DarkPanel)
            ) {
                if (song.cover != null) {
                    val bitmap = remember(song.cover) {
                        BitmapFactory.decodeByteArray(song.cover, 0, song.cover.size)
                    }
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp).align(Alignment.Center),
                        tint = HiFiColors.Sand
                    )
                }
            }

            Spacer(modifier = Modifier.width(HiFiDimensions.Medium))

            // Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = HiFiColors.Ivory,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = HiFiColors.Sand,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Controles
            IconButton(onClick = { onEvent(PlayerEvent.ToggleFavorite(song)) }) {
                Icon(
                    imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (state.isFavorite) HiFiColors.Copper else HiFiColors.Ivory,
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(onClick = { onEvent(PlayerEvent.PlayPause) }) {
                Icon(
                    imageVector = if (state.playbackStatus == PlaybackStatus.PLAYING) 
                        Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = HiFiColors.Ivory,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(onClick = { onEvent(PlayerEvent.Next) }) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = null,
                    tint = HiFiColors.Ivory,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

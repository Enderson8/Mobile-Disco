package com.example.mobiledisco.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.mobiledisco.data.Song
import com.example.mobiledisco.ui.theme.HiFiColors
import com.example.mobiledisco.ui.theme.HiFiDimensions

@Composable
fun SongListItem(
    song: Song,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) HiFiColors.DarkPanel else HiFiColors.Espresso)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = HiFiDimensions.Medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Barra lateral de seleção (estilo indicador Hi-Fi)
            if (isSelected) {
                Spacer(modifier = Modifier.width(HiFiDimensions.Small))
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(HiFiColors.Copper)
                )
            } else {
                // Espaçador para manter o alinhamento quando não selecionado
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = HiFiDimensions.Medium)
                    .weight(1f)
            ) {
                Text(
                    text = if (isSelected) "▶ ${song.name}" else song.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = HiFiColors.Ivory
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = HiFiColors.Sand
                )
            }
        }
    }
}

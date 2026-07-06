package com.example.mobiledisco.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mobiledisco.data.Song
import com.example.mobiledisco.ui.theme.HiFiColors
import com.example.mobiledisco.ui.theme.HiFiDimensions

@Composable
fun SongListItem(
    song: Song,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(HiFiDimensions.Small)
            .clickable {
                onClick()
            }
    ) {
        Text(
            text = if (isSelected)
                "▶ ${song.name}"
            else
                song.name,
            style = if (isSelected)
                MaterialTheme.typography.titleMedium
            else
                MaterialTheme.typography.bodyLarge
        )
        Text(
            text = song.artist,
            style = MaterialTheme.typography.bodySmall,
            color = HiFiColors.PanelGray
        )
    }
}

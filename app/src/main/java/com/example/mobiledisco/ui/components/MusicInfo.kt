package com.example.mobiledisco.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.mobiledisco.data.Song
import com.example.mobiledisco.ui.theme.HiFiColors
import com.example.mobiledisco.ui.theme.HiFiDimensions

@Composable
fun MusicInfo(
    musica: Song?
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = musica?.name ?: "Selecione uma música",
            style = MaterialTheme.typography.headlineSmall,
            color = HiFiColors.Ivory,
            textAlign = TextAlign.Center
        )

        if (musica != null) {
            Spacer(modifier = Modifier.height(HiFiDimensions.ExtraSmall))

            Text(
                text = musica.artist,
                style = MaterialTheme.typography.titleMedium,
                color = HiFiColors.Sand,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(HiFiDimensions.Tiny))

            Text(
                text = musica.album,
                style = MaterialTheme.typography.bodySmall,
                color = HiFiColors.SoftBrown,
                textAlign = TextAlign.Center
            )
        }
    }
}

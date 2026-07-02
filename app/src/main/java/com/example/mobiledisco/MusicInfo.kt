package com.example.mobiledisco

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MusicInfo(
    musica: Song?
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = musica?.name ?: "Nenhuma música selecionada",
            style = MaterialTheme.typography.titleLarge
        )

        if (musica != null) {

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = musica.artist,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = musica.album,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

        }

    }

}

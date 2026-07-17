package com.example.mobiledisco.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
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
        AnimatedContent(
            targetState = musica?.name ?: "Selecione uma música",
            transitionSpec = {
                fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
            },
            label = "nameAnimation"
        ) { text ->
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
                color = HiFiColors.Ivory,
                textAlign = TextAlign.Center
            )
        }

        if (musica != null) {
            Spacer(modifier = Modifier.height(HiFiDimensions.ExtraSmall))

            AnimatedContent(
                targetState = musica.artist,
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
                },
                label = "artistAnimation"
            ) { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    color = HiFiColors.Sand,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(HiFiDimensions.Tiny))

            AnimatedContent(
                targetState = musica.album,
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
                },
                label = "albumAnimation"
            ) { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = HiFiColors.SoftBrown,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

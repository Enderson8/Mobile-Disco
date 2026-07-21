package com.example.mobiledisco.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.mobiledisco.data.Song
import com.example.mobiledisco.ui.theme.HiFiColors
import com.example.mobiledisco.ui.theme.HiFiDimensions

private val bitmapCache = LruCache<Int, Bitmap>(20)

@Composable
fun AlbumCover(
    musica: Song?,
    isPlaying: Boolean = false,
    onClick: () -> Unit = {}
) {
    // Animação de "pulso" sutil quando estiver tocando
    val scale by animateFloatAsState(
        targetValue = if (isPlaying) 1.03f else 1f,
        animationSpec = if (isPlaying) {
            infiniteRepeatable(
                animation = tween(durationMillis = 2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        } else {
            tween(durationMillis = 500)
        },
        label = "coverPulse"
    )

    AnimatedContent(
        targetState = musica?.uri to musica?.cover,
        transitionSpec = {
            fadeIn(animationSpec = tween(durationMillis = 500)) togetherWith fadeOut(animationSpec = tween(durationMillis = 500))
        },
        label = "coverAnimation"
    ) { (uri, coverBytes) ->
        if (coverBytes != null) {
            val bitmap = remember(coverBytes) {
                val key = uri.hashCode()
                bitmapCache.get(key) ?: BitmapFactory.decodeByteArray(coverBytes, 0, coverBytes.size)?.also {
                    bitmapCache.put(key, it)
                }
            } ?: return@AnimatedContent
            
            Box(
                modifier = Modifier
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .clickable { onClick() }
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(HiFiDimensions.Normal)
                    )
                    .border(
                        BorderStroke(1.dp, HiFiColors.CopperDark),
                        shape = RoundedCornerShape(HiFiDimensions.Normal)
                    )
                    .padding(4.dp)
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Capa do álbum",
                    modifier = Modifier
                        .size(HiFiDimensions.AlbumCoverSize)
                        .clip(RoundedCornerShape(HiFiDimensions.Small))
                )
            }
        }
    }
}


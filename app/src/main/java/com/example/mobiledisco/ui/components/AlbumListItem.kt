package com.example.mobiledisco.ui.components

import android.graphics.BitmapFactory
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mobiledisco.data.Album
import com.example.mobiledisco.ui.theme.HiFiColors
import com.example.mobiledisco.ui.theme.HiFiDimensions

@Composable
fun AlbumListItem(
    album: Album?, // Tornando o álbum opcional por segurança
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    // Se o álbum for nulo (não deveria ser, mas protege de crashes), não desenha nada
    if (album == null) return

    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        animationSpec = tween(300),
        label = "arrowRotation"
    )

    // Otimização e Blindagem: Decodifica apenas se necessário e trata nulos com segurança
    val albumBitmap = remember(album.cover) {
        album.cover?.let { bytes ->
            try {
                // decodeByteArray pode retornar null se os bytes forem inválidos
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(HiFiDimensions.Medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Indicador de expansão animado
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = HiFiColors.Copper,
            modifier = Modifier
                .size(24.dp)
                .rotate(rotation)
        )

        Spacer(modifier = Modifier.width(HiFiDimensions.Small))

        // Mini Capa do Álbum
        Box(
            modifier = Modifier
                .size(HiFiDimensions.AlbumThumbnailSize)
                .clip(RoundedCornerShape(HiFiDimensions.ExtraSmall))
                .border(1.dp, HiFiColors.CopperDark, RoundedCornerShape(HiFiDimensions.ExtraSmall))
                .background(HiFiColors.DarkPanel)
        ) {
            if (albumBitmap != null) {
                Image(
                    bitmap = albumBitmap,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).align(Alignment.Center),
                    alpha = 0.5f
                )
            }
        }

        Spacer(modifier = Modifier.width(HiFiDimensions.Medium))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = album.name ?: "Álbum desconhecido",
                style = MaterialTheme.typography.titleMedium,
                color = HiFiColors.Ivory
            )
            
            Text(
                text = album.artist ?: "Artista desconhecido",
                style = MaterialTheme.typography.bodyMedium,
                color = HiFiColors.Sand
            )

            Text(
                text = "${album.songs?.size ?: 0} faixas",
                style = MaterialTheme.typography.bodySmall,
                color = HiFiColors.SoftBrown
            )
        }
    }
}

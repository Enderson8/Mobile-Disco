package com.example.mobiledisco.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.mobiledisco.data.Song
import com.example.mobiledisco.ui.theme.HiFiColors
import com.example.mobiledisco.ui.theme.HiFiDimensions

@Composable
fun AlbumCover(
    musica: Song?,
    onClick: () -> Unit = {}
) {
    musica?.cover?.let { bytes ->
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        
        Box(
            modifier = Modifier
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

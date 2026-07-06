package com.example.mobiledisco.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import com.example.mobiledisco.data.Song
import com.example.mobiledisco.ui.theme.HiFiDimensions

@Composable
fun AlbumCover(
    musica: Song?
) {
    musica?.cover?.let { bytes ->
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Capa do álbum",
            modifier = Modifier
                .shadow(
                    elevation = HiFiDimensions.Normal,
                    shape = RoundedCornerShape(HiFiDimensions.Normal)
                )
                .clip(RoundedCornerShape(HiFiDimensions.Normal))
                .size(HiFiDimensions.AlbumCoverSize)
        )
        Spacer(modifier = Modifier.height(HiFiDimensions.CardPadding))
    }
}

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
import androidx.compose.ui.unit.dp
import com.example.mobiledisco.data.Song

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
                    elevation = 12.dp,
                    shape = RoundedCornerShape(12.dp)
                )
                .clip(RoundedCornerShape(12.dp))
                .size(240.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}

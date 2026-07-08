package com.example.mobiledisco.data

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri

object MusicMetadata {

    fun read(
        context: Context,
        uri: Uri
    ): SongMetadata {

        val retriever = MediaMetadataRetriever()

        return try {

            retriever.setDataSource(
                context,
                uri
            )

            val cover = retriever.embeddedPicture

            // Extrair número da faixa (pode vir como "1", "01" ou "1/10")
            val rawTrack = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)
            val trackNumber = rawTrack?.split("/")?.firstOrNull()?.toIntOrNull() ?: 0

            SongMetadata(
                title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: "Título desconhecido",
                artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Artista desconhecido",
                album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "Álbum desconhecido",
                duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L,
                cover = cover,
                trackNumber = trackNumber
            )

        } finally {
            retriever.release()
        }
    }
}

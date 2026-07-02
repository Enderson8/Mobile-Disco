package com.example.mobiledisco

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

            println(
                "Tem capa? ${cover != null}"
            )

            SongMetadata(

                title =
                    retriever.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_TITLE
                    ) ?: "Título desconhecido",

                artist =
                    retriever.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_ARTIST
                    ) ?: "Artista desconhecido",

                album =
                    retriever.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_ALBUM
                    ) ?: "Álbum desconhecido",

                duration =
                    retriever.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_DURATION
                    )?.toLongOrNull() ?: 0L,

                cover = cover

            )

        } finally {

            retriever.release()

        }

    }

}

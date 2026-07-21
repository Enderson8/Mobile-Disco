package com.example.mobiledisco.importer

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.example.mobiledisco.data.MusicMetadata
import com.example.mobiledisco.data.Song
import com.example.mobiledisco.data.toSong

object MusicImporter {

    fun importAlbum(context: Context, folderUri: Uri): List<Song> {
        val rootFolder = DocumentFile.fromTreeUri(context, folderUri)

        return if (rootFolder != null && rootFolder.isDirectory) {
            rootFolder.listFiles()
                .filter { it.isFile && isAudioFile(it) }
                .mapNotNull { file ->
                    try {
                        val metadata = MusicMetadata.read(context, file.uri)
                        metadata.toSong(file.uri)
                    } catch (_: Exception) {
                        null
                    }
                }
        } else {
            emptyList()
        }
    }

    private fun isAudioFile(file: DocumentFile): Boolean {
        val mimeType = file.type
        return mimeType != null && mimeType.startsWith("audio/")
    }
}

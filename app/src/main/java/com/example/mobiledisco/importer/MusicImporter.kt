package com.example.mobiledisco.importer

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.example.mobiledisco.data.MusicMetadata
import com.example.mobiledisco.data.Song
import com.example.mobiledisco.data.toSong

object MusicImporter {

    fun importAlbum(context: Context, folderUri: Uri): List<Song> {
        val songs = mutableListOf<Song>()
        val rootFolder = DocumentFile.fromTreeUri(context, folderUri)

        if (rootFolder != null && rootFolder.isDirectory) {
            rootFolder.listFiles().forEach { file ->
                if (file.isFile && isAudioFile(file)) {
                    try {
                        val metadata = MusicMetadata.read(context, file.uri)
                        songs.add(metadata.toSong(file.uri))
                    } catch (e: Exception) {
                        println("Erro ao importar música: ${file.name}")
                    }
                }
            }
        }
        return songs
    }

    private fun isAudioFile(file: DocumentFile): Boolean {
        val mimeType = file.type
        return mimeType != null && mimeType.startsWith("audio/")
    }
}

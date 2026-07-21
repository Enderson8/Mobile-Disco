package com.example.mobiledisco.data

import android.content.Context
import androidx.core.content.edit

class PlaylistRepository(context: Context) {
    private val prefs = context.getSharedPreferences("mobile_disco_playlists", Context.MODE_PRIVATE)

    fun savePlaylists(playlists: List<Playlist>) {
        prefs.edit {
            clear()
            playlists.forEach { playlist ->
                // Format: id|name|uri1;uri2;uri3
                val songsUris = playlist.songs.joinToString(";") { it.uri }
                val data = "${playlist.id}|${playlist.name}|$songsUris"
                putString(playlist.id.toString(), data)
            }
        }
    }

    fun loadPlaylists(allSongs: List<Song>): List<Playlist> {
        return prefs.all.values.asSequence()
            .mapNotNull { value ->
                try {
                    val data = value.toString().split("|")
                    if (data.size >= 2) {
                        val id = data[0].toLong()
                        val name = data[1]
                        val uris = if (data.size > 2) data[2].split(";").filter { it.isNotEmpty() } else emptyList()
                        
                        val playlistSongs = uris.mapNotNull { uri -> 
                            allSongs.find { it.uri == uri }
                        }
                        
                        Playlist(id, name, playlistSongs)
                    } else null
                } catch (_: Exception) {
                    null
                }
            }
            .sortedBy { it.name }
            .toList()
    }
}

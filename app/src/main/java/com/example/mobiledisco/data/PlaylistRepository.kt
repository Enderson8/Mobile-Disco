package com.example.mobiledisco.data

import android.content.Context

class PlaylistRepository(context: Context) {
    private val prefs = context.getSharedPreferences("mobile_disco_playlists", Context.MODE_PRIVATE)

    fun savePlaylists(playlists: List<Playlist>) {
        val editor = prefs.edit()
        editor.clear()
        playlists.forEach { playlist ->
            // Format: id|name|uri1;uri2;uri3
            val songsUris = playlist.songs.joinToString(";") { it.uri }
            val data = "${playlist.id}|${playlist.name}|$songsUris"
            editor.putString(playlist.id.toString(), data)
        }
        editor.apply()
    }

    fun loadPlaylists(allSongs: List<Song>): List<Playlist> {
        val playlists = mutableListOf<Playlist>()
        prefs.all.forEach { (_, value) ->
            try {
                val data = value.toString().split("|")
                if (data.size >= 2) {
                    val id = data[0].toLong()
                    val name = data[1]
                    val uris = if (data.size > 2) data[2].split(";").filter { it.isNotEmpty() } else emptyList()
                    
                    val playlistSongs = uris.mapNotNull { uri -> 
                        allSongs.find { it.uri == uri }
                    }
                    
                    playlists.add(Playlist(id, name, playlistSongs))
                }
            } catch (e: Exception) {
                // Ignore malformed data
            }
        }
        return playlists.sortedBy { it.name }
    }
}

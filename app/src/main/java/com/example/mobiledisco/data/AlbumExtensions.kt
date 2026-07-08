package com.example.mobiledisco.data

fun List<Song>.toAlbums(): List<Album> {
    return this.groupBy { it.album to it.artist }
        .map { (info, songs) ->
            Album(
                name = info.first,
                artist = info.second,
                cover = songs.firstOrNull { it.cover != null }?.cover ?: songs.firstOrNull()?.cover,
                songs = songs.sortedBy { it.trackNumber }
            )
        }
        .sortedBy { it.name }
}

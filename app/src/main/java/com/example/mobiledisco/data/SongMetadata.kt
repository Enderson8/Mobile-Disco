package com.example.mobiledisco.data

import android.net.Uri

data class SongMetadata(
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val cover: ByteArray?,
    val trackNumber: Int = 0
)

fun SongMetadata.toSong(uri: Uri): Song = Song(
    name = title,
    artist = artist,
    album = album,
    duration = duration,
    uri = uri.toString(),
    id = uri.toString().hashCode().toLong(),
    cover = cover,
    trackNumber = trackNumber
)

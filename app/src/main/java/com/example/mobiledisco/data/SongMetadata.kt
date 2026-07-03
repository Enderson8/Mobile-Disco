package com.example.mobiledisco.data

data class SongMetadata(
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val cover: ByteArray?
)

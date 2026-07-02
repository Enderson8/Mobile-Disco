package com.example.mobiledisco

data class SongMetadata(
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val cover: ByteArray?
)

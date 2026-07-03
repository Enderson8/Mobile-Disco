package com.example.mobiledisco.data

data class Song(
    val name: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val uri: String,
    val id: Long,
    val cover: ByteArray?
)

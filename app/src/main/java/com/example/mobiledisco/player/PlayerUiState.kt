package com.example.mobiledisco.player

import com.example.mobiledisco.data.Song

data class PlayerUiState(
    val musica: Song? = null,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val isPlaying: Boolean = false
)

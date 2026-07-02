package com.example.mobiledisco

data class PlayerUiState(
    val musica: Song? = null,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val isPlaying: Boolean = false
)

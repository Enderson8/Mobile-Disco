package com.example.mobiledisco.player

sealed interface PlayerEvent {

    data object PlayPause : PlayerEvent

    data object Stop : PlayerEvent

    data object Next : PlayerEvent

    data object Previous : PlayerEvent

    data class Seek(
        val position: Long
    ) : PlayerEvent

}

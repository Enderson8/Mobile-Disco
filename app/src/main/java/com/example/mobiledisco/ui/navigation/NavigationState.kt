package com.example.mobiledisco.ui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class NavigationState {

    var currentScreen by mutableStateOf(AppScreen.LIBRARY)
        private set

    var selectedPlaylistId by mutableStateOf<Long?>(null)
        private set

    fun openLibrary() {
        currentScreen = AppScreen.LIBRARY
        selectedPlaylistId = null
    }

    fun openNowPlaying() {
        currentScreen = AppScreen.NOW_PLAYING
    }

    fun openPlaylist(id: Long) {
        selectedPlaylistId = id
        currentScreen = AppScreen.PLAYLIST
    }

}
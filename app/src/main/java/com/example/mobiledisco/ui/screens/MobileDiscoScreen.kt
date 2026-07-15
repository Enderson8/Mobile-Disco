package com.example.mobiledisco.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobiledisco.player.PlaybackStatus
import com.example.mobiledisco.player.PlayerUiState
import com.example.mobiledisco.ui.navigation.AppScreen
import com.example.mobiledisco.ui.navigation.NavigationState
import com.example.mobiledisco.ui.theme.MobileDiscoTheme
import com.example.mobiledisco.viewmodel.MusicViewModel

@Composable
fun MobileDiscoScreen(
    modifier: Modifier = Modifier
) {
    val navigation = remember { NavigationState() }
    val viewModel: MusicViewModel = viewModel()
    
    val isPlaying by viewModel.isPlaying.collectAsState()
    val musicaSelecionada by viewModel.musicaSelecionada.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val isShuffleEnabled by viewModel.isShuffleEnabled.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()

    Crossfade(targetState = navigation.currentScreen, label = "screenTransition") { screen ->
        when (screen) {
            AppScreen.LIBRARY -> {
                HomeScreenContent(
                    viewModel = viewModel,
                    onCoverClick = { navigation.openNowPlaying() }
                )
            }
            AppScreen.NOW_PLAYING -> {
                BackHandler {
                    navigation.openLibrary()
                }
                NowPlayingScreen(
                    state = PlayerUiState(
                        musica = musicaSelecionada,
                        currentPosition = currentPosition,
                        duration = duration,
                        playbackStatus = if (isPlaying) PlaybackStatus.PLAYING else PlaybackStatus.STOPPED,
                        isShuffleEnabled = isShuffleEnabled,
                        repeatMode = repeatMode
                    ),
                    onEvent = { event -> viewModel.handlePlayerEvent(event) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MobileDiscoPreview() {
    MobileDiscoTheme {
        MobileDiscoScreen()
    }
}

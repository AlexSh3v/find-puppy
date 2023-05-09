package com.alexsh3v.findpuppy.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.alexsh3v.findpuppy.FindPuppyGame
import com.alexsh3v.findpuppy.VibrationMode

@Composable
fun App(game: FindPuppyGame, vibrationCallback: (VibrationMode) -> Unit) {
    val screenType by game.screenType.collectAsState()

    when (screenType) {
        FindPuppyGame.ScreenType.Menu -> Menu(game)
        FindPuppyGame.ScreenType.Game -> Game(game, vibrationCallback = vibrationCallback)
    }

}

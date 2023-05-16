package com.alexsh3v.findpuppy.game

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alexsh3v.findpuppy.FindPuppyGame
import com.alexsh3v.findpuppy.VibrationMode
import com.alexsh3v.findpuppy.utils.Screen

@Composable
fun App(game: FindPuppyGame, vibrationCallback: (VibrationMode) -> Unit) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Menu.route,
    ) {
        composable(route = Screen.Menu.route) {
            Menu(
                game = game,
                vibrationCallback = vibrationCallback,
                navController = navController
            )
        }
        composable(Screen.Game.route) {
            Game(
                game = game,
                vibrationCallback = vibrationCallback,
                navController = navController
            )
        }
    }
    
//    val screenType by game.screenType.collectAsState()
//
//    when (screenType) {
//        FindPuppyGame.ScreenType.Menu -> Menu(game, vibrationCallback)
//        FindPuppyGame.ScreenType.Game -> Game(game, vibrationCallback = vibrationCallback)
//    }

}

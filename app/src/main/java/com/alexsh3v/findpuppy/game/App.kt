package com.alexsh3v.findpuppy.game

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alexsh3v.findpuppy.FindPuppyGame
import com.alexsh3v.findpuppy.VibrationMode
import com.alexsh3v.findpuppy.utils.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun App(game: FindPuppyGame, vibrationCallback: (VibrationMode) -> Unit) {
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    var isLoading by remember {
        mutableStateOf(false)
    }
    var loadingMobId by remember {
        mutableStateOf(com.alexsh3v.findpuppy.R.raw.puppy)
    }
    var loadingBgColor by remember {
        mutableStateOf(Color.Green)
    }

    val loadingScreenEvents = object : LoadingScreenEvents {
        override fun load(durationMillis: Int) {
            scope.launch {
                isLoading = true
                delay(durationMillis.toLong())
                isLoading = false
            }
        }

        override fun changeMob(resourceId: Int, backgroundColor: Color) {
            loadingMobId = resourceId
            loadingBgColor = backgroundColor
        }
    }

    LoadingScreen(
        isActivated = isLoading,
        resourceId = loadingMobId,
        backgroundColor = loadingBgColor
    )
    
    NavHost(
        navController = navController,
        startDestination = Screen.Menu.route,
    ) {
        composable(route = Screen.Menu.route) {
            Menu(
                game = game,
                vibrationCallback = vibrationCallback,
                navController = navController,
                loadingScreenEvents = loadingScreenEvents
            )
        }
        composable(Screen.Game.route) {
            Game(
                game = game,
                vibrationCallback = vibrationCallback,
                navController = navController,
                loadingScreen = loadingScreenEvents
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

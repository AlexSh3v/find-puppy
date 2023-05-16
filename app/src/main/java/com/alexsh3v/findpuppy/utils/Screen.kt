package com.alexsh3v.findpuppy.utils

sealed class Screen(val route: String) {
    object Menu : Screen("menu_screen")
    object Game : Screen("game_screen")
}

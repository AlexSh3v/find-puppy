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
}

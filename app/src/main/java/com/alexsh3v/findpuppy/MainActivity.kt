package com.alexsh3v.findpuppy

import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.alexsh3v.findpuppy.game.App
import com.alexsh3v.findpuppy.game.StatusBar
import com.alexsh3v.findpuppy.ui.theme.FindPuppyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val game = FindPuppyGame()
        setContent {
            FindPuppyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0A4C09)
                ) {
                }
                App(game, vibrationCallback = { mode -> vibrate(mode) })
            }
        }
    }

    private fun vibrate(vibrationMode: VibrationMode) {
        val vibrator: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(
            when (vibrationMode) {
                VibrationMode.PuppySpotted -> VibrationEffect.createOneShot(
                    250,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
                VibrationMode.EnemyNearby -> VibrationEffect.createOneShot(500, 100)

                else -> VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FindPuppyTheme {
        var stepsCounter by remember {
            mutableStateOf(0)
        }
        StatusBar(
            stepsCounter = { stepsCounter },
            timePassedInSeconds = { 0 },
            onPauseButtonClick = { stepsCounter++ },
            isDebug = true
        )
    }
}
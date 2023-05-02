package com.alexsh3v.findpuppy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alexsh3v.findpuppy.game.App
import com.alexsh3v.findpuppy.ui.theme.FindPuppyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FindPuppyTheme {
                val viewModel = viewModel<AppViewModel>()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0A4C09)
                ) {
                }
                App(gameViewModel = viewModel)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FindPuppyTheme {
    }
}
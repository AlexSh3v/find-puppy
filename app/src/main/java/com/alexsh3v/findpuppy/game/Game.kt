package com.alexsh3v.findpuppy

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource

@Composable
fun App(gameViewModel: AppViewModel) {
    val screenType by gameViewModel.screenType.collectAsState()

    when (screenType) {
        AppViewModel.ScreenType.Menu -> Menu(gameViewModel)
        AppViewModel.ScreenType.Game -> Game(gameViewModel)
    }

}


@Composable
fun Menu(gameViewModel: AppViewModel) {
}

@Composable
fun Game(gameViewModel: AppViewModel) {
    // FIXME: put into remember IF during the game there will be some recompositions
    gameViewModel.generateNewField()

    val cellImage = ImageBitmap.imageResource(id = R.drawable.cell_default)

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFAAAAAA))
    ) {

        drawImage(cellImage)
    }

//    gameViewModel.forEachInRadius { x, y, cell ->
//
//    }

}
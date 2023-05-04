package com.alexsh3v.findpuppy.game

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.alexsh3v.findpuppy.FindPuppyGame
import com.alexsh3v.findpuppy.FindPuppyGame.Companion.FIELD_SIZE
import com.alexsh3v.findpuppy.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlin.math.abs

@Composable
fun App(game: FindPuppyGame) {
    val screenType by game.screenType.collectAsState()

    when (screenType) {
        FindPuppyGame.ScreenType.Menu -> Menu(game)
        FindPuppyGame.ScreenType.Game -> Game(game)
    }

}


@Composable
fun Menu(gameViewModel: FindPuppyGame) {
}

@Composable
fun Game(game: FindPuppyGame) {

    remember {
        game.generateNewField()
        null
    }

    var tileSize by remember {
        mutableStateOf(100.dp)
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp


//    //
//    Image(
//        bitmap = cellStatic,
//        contentDescription = "",
//        contentScale = ContentScale.Fit,
//        modifier = Modifier
//            .size(tileSize)
//            .offset(
//                x = screenWidth
//                    .div(2)
//                    .minus(tileSize.div(2)),
//                y = screenHeight
//                    .div(2)
//                    .minus(tileSize.div(2))
//            )
//            .clickable {
//                tileSize = tileSize.plus(5.dp)
//            }
//    )
//    // Image 2
//    Image(
//        bitmap = cellStatic,
//        contentDescription = "",
//        contentScale = ContentScale.Fit,
//        modifier = Modifier
//            .size(tileSize)
//            .padding(0.dp)
//            .offset(tileSize, 0.dp)
//    )
//    Image(
//        bitmap = cellStatic,
//        contentDescription = "",
//        contentScale = ContentScale.Fit,
//        modifier = Modifier
//            .size(tileSize)
//            .padding(0.dp)
//            .offset(tileSize.times(2), 0.dp)
//    )

//    TestCellInCenter(cellStatic, tileSize)

    val scope = rememberCoroutineScope()
    var isPuppyFound by remember {
        mutableStateOf(false)
    }
    val centerX = screenWidth.div(2).minus(tileSize.div(2))
    val centerY = screenHeight.div(2).minus(tileSize.div(2))
    val selectedCell = game.selectedCell.collectAsState().value
    val selectedI = selectedCell.i.collectAsState(initial = 0).value
    val selectedJ = selectedCell.j.collectAsState(initial = 0).value

    for (i in 0 until FIELD_SIZE) {
        for (j in 0 until FIELD_SIZE) {

            val cellObject = game.getCellAt(i, j)
            cellObject.bindPosition(i, j)

            val resourceState = remember {
                mutableStateOf(R.raw.cell_static)
            }.also {
                selectImageByCell(cellObject = cellObject, resourceState = it)
            }

            val type = cellObject.type.collectAsState(initial = Cell.Type.WithPuppy).value

            val relativeVector = Pair(
                i - selectedI, // x
                j - selectedJ // y
            )
            val distance = abs(relativeVector.first) + abs(relativeVector.second)

            val relativeCoordinates = Pair(
                tileSize.times(relativeVector.first).plus(centerX), // x
                tileSize.times(relativeVector.second).plus(centerY) // y
            )

            var modifier = Modifier
                .size(tileSize)
                .offset(
                    x = relativeCoordinates.first,
                    y = relativeCoordinates.second
                )

            var colorFilter: ColorFilter? = null

            // If distance to cell is zero
            // that means that it is the selected cell.
            // So we make yellow border to it
            if (distance == 0) {
                modifier = modifier.border(3.dp, Color.Yellow)
            }

            // For cell that are to the left, right, above and bottom
            if (!isPuppyFound && distance == 1) {

                colorFilter = ColorFilter.tint(
                    Color(0x43000000),
                    blendMode = BlendMode.Darken
                )

                modifier = modifier.clickable {
                    if (i == 0 && 0 == j)
                        tileSize = tileSize.plus(5.dp)
                    if (i == 0 && 1 == j)
                        tileSize = tileSize.minus(5.dp)

                    cellObject.changeState(Cell.State.Shown)
                    Log.d("FindPuppyGame", "clicked at: $i $j")
                    selectedCell.bindPosition(i, j)


                    if (type == Cell.Type.WithPuppy) {
                        scope.launch {
                            isPuppyFound = true
                            delay(2000)
                            isPuppyFound = false
                            game.generateNewField()
                        }
                    }
                }
            }

            Image(
                painter = painterResource(id = resourceState.value),
                contentDescription = "image at $i $j",
                contentScale = ContentScale.Fit,
                modifier = modifier,
                colorFilter = colorFilter
            )

        }

    }


}

@Composable
fun selectImageByCell(cellObject: Cell, resourceState: MutableState<Int>) {
    val type = cellObject.type.collectAsState(initial = Cell.Type.Neutral).value
    val state = cellObject.state.collectAsState(initial = Cell.State.Hidden).value

    resourceState.value = when (state) {
        Cell.State.Hidden -> R.raw.gress_cell

        Cell.State.Shown -> when (type) {
            Cell.Type.WithPuppy -> R.raw.puppy
            else -> R.raw.cell_pressed
        }

        else -> R.raw.cell_pressed
    }
}

@Composable
fun drawWithSelectedInCenter() {
    for (i in 0 until FIELD_SIZE) {
        for (j in 0 until FIELD_SIZE) {

        }
    }
}

@Composable
fun TestCellInCenter(cellStatic: ImageBitmap, tileSize: Dp) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    var tileSizeCopied by remember {
        mutableStateOf(tileSize)
    }

    val centerX = screenWidth
        .div(2)
        .minus(tileSizeCopied.div(2))
    val centerY = screenHeight
        .div(2)
        .minus(tileSizeCopied.div(2))

    Image(
        bitmap = cellStatic,
        contentDescription = "",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(tileSizeCopied)
            .offset(
                x = centerX,
                y = centerY
            )
            .clickable {
                tileSizeCopied = tileSizeCopied.plus(5.dp)
            }
    )

    // Above centered
    Image(
        bitmap = cellStatic,
        contentDescription = "",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(tileSizeCopied)
            .offset(
                x = centerX,
                y = centerY.minus(tileSizeCopied)
            )
            .clickable {
            }
    )

}

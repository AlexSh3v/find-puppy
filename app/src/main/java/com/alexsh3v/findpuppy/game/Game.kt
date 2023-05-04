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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.alexsh3v.findpuppy.FindPuppyGame
import com.alexsh3v.findpuppy.FindPuppyGame.Companion.FIELD_SIZE
import com.alexsh3v.findpuppy.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    val scope = rememberCoroutineScope()
    var isPuppyFound by remember {
        mutableStateOf(false)
    }
    val centerX = screenWidth.div(2).minus(tileSize.div(2))
    val centerY = screenHeight.div(2).minus(tileSize.div(2))
    val selectedTile = game.selectedTile.collectAsState().value
    val selectedTileVector = Pair(
        selectedTile.i.collectAsState(initial = 0).value,
        selectedTile.j.collectAsState(initial = 0).value
    )

    for (i in 0 until FIELD_SIZE) {
        for (j in 0 until FIELD_SIZE) {

            val tileObject = game.getTileAt(i, j)
            tileObject.bindPosition(i, j)

            val resourceState = remember {
                mutableStateOf(R.raw.debug_tile)
            }.also {
                SelectImageByTile(tileObject = tileObject, resourceState = it)
            }

            val type = tileObject.type.collectAsState(initial = Tile.Type.WithPuppy).value

            val relativeVector = Pair(
                i - selectedTileVector.first, // x
                j - selectedTileVector.second // y
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

            // If distance to tile is zero
            // that means that it is the selected tile.
            // So we make yellow border to it
            if (distance == 0) {
                modifier = modifier.border(3.dp, Color.Yellow)
            }

            // For tile that are to the left, right, above and bottom
            if (!isPuppyFound && distance == 1) {

                colorFilter = ColorFilter.tint(
                    Color(0x43000000), // todo: export color
                    blendMode = BlendMode.Darken
                )

                modifier = modifier.clickable {
                    if (i == 0 && 0 == j)
                        tileSize = tileSize.plus(5.dp)
                    if (i == 0 && 1 == j)
                        tileSize = tileSize.minus(5.dp)

                    tileObject.changeState(Tile.State.Shown)
                    Log.d("FindPuppyGame", "clicked at: $i $j")
                    selectedTile.bindPosition(i, j)


                    if (type == Tile.Type.WithPuppy) {
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
fun SelectImageByTile(tileObject: Tile, resourceState: MutableState<Int>) {
    val type = tileObject.type.collectAsState(initial = Tile.Type.Neutral).value
    val state = tileObject.state.collectAsState(initial = Tile.State.Hidden).value

    resourceState.value = when (state) {
        Tile.State.Hidden -> R.raw.grass_default_tile

        Tile.State.Shown -> when (type) {
            Tile.Type.WithPuppy -> R.raw.puppy
            else -> R.raw.grass_pressed_tile
        }

        else -> R.raw.grass_pressed_tile
    }
}

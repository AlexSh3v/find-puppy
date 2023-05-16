package com.alexsh3v.findpuppy.game

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.alexsh3v.findpuppy.FindPuppyGame
import com.alexsh3v.findpuppy.R
import com.alexsh3v.findpuppy.VibrationMode
import com.alexsh3v.findpuppy.ui.theme.AllowToGo
import com.alexsh3v.findpuppy.utils.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@SuppressLint("ResourceType")
@Composable
fun Game(
    game: FindPuppyGame,
    vibrationCallback: (VibrationMode) -> Unit,
    navController: NavHostController
) {

    remember {
        game.generateNewField()
        null
    }

    val tileSize by remember {
        mutableStateOf(100.dp)
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val scope = rememberCoroutineScope()
    var isGameSuspendedNecessarily by remember {
        mutableStateOf(false)
    }
    var isPuppyFound by remember {
        mutableStateOf(false)
    }
    var isEnemyFound by remember {
        mutableStateOf(false)
    }
    var isPaused by remember {
        mutableStateOf(false)
    }
    val centerX = screenWidth.div(2).minus(tileSize.div(2))
    val centerY = screenHeight.div(2).minus(tileSize.div(2))
    val selectedTile = game.selectedTile.collectAsState().value
    val selectedTileVector = Pair(
        selectedTile.i.collectAsState(initial = 0).value,
        selectedTile.j.collectAsState(initial = 0).value
    )

    var stepsCounter by remember {
        mutableStateOf(0)
    }
    val clickedVector = remember {
        mutableStateOf(Pair(centerX, centerY))
    }
    var isNearestTilePressed by remember {
        mutableStateOf(false)
    }
    val selectedBorderX by animateDpAsState(
        targetValue = if (isNearestTilePressed && !isPuppyFound && !isEnemyFound) clickedVector.value.first else centerX,
        animationSpec = tween(
            durationMillis = 500,
        )
    )
    val selectedBorderY by animateDpAsState(
        targetValue = if (isNearestTilePressed && !isPuppyFound && !isEnemyFound) clickedVector.value.second else centerY,
        animationSpec = tween(
            durationMillis = 500,
        )
    )

    var borderResource by remember {
        mutableStateOf(R.raw.selected_border)
    }
    val borderResourceDerived by remember {
        derivedStateOf { borderResource }
    }
    SelectedBorder(
        resourceId = {
            Log.d(
                FindPuppyGame.TAG, "New border res -> ${
                    when (borderResourceDerived) {
                        R.raw.selected_border -> "YELLOW"
                        else -> "RED"
                    }
                }"
            )
            borderResourceDerived
        },
        tileSize = { tileSize },
        offsetX = { selectedBorderX },
        offsetY = { selectedBorderY }
    )

    StatusBar(
        stepsCounter = { stepsCounter },
        timePassedInSeconds = { 0 },
        onPauseButtonClick = {
            isPaused = true
        }
    )

    PausePopupWindow(
        isPaused = isPaused,
        buttonSize = 100.dp,
        gapWidth = 25.dp,
        onReturnButton = {
            isPaused = false
        },
        onMenuButton = {
            navController.navigate(Screen.Menu.route)
        }
    )

    for (i in 0 until game.totalFieldSize) {
        for (j in 0 until game.totalFieldSize) {

            val tileObject = game.getTileAt(i, j)
            tileObject.bindPosition(i, j)

            Log.d("FindPuppyGame", "recomposition!")

            val resourceState = remember {
                mutableStateOf(R.raw.debug_tile)
            }.also {
                SelectImageByTile(tileObject = tileObject, resourceState = it)
            }

            val type = tileObject.type.collectAsState(initial = Tile.Type.Neutral).value

            val relativeVector = Pair(
                i - selectedTileVector.first, // x
                j - selectedTileVector.second // y
            )
            val distance = abs(relativeVector.first) + abs(relativeVector.second)

            val newCalculatedVector = Pair(
                tileSize.times(relativeVector.first).plus(centerX), // x
                tileSize.times(relativeVector.second).plus(centerY) // y
            )

            var colorFilter: ColorFilter? = null

            borderResource = if (isEnemyFound) {
                R.raw.selected_red_border
            } else {
                R.raw.selected_border
            }

            if (distance == 1 && tileObject.isEnemy() && !isPuppyFound)
                remember {
                    vibrationCallback(VibrationMode.EnemyNearby)
                    null
                }

            val isClickable = !isGameSuspendedNecessarily
                    && distance == 1
                    && !tileObject.isDecoration()
                    && type != Tile.Type.Dirt

            if (isClickable) {
                colorFilter = ColorFilter.tint(
                    AllowToGo,
                    blendMode = BlendMode.Darken
                )
            }

            val isOnScreen = isPositionInScreen(
                screenConfig = configuration,
                vectorPair = newCalculatedVector,
                additionalRadius = 200.dp
            )

            val animateX by animateDpAsState(
                targetValue = newCalculatedVector.first,
                animationSpec = tween(
                    durationMillis = 500,
                )
            )

            val animateY by animateDpAsState(
                targetValue = newCalculatedVector.second,
                animationSpec = tween(
                    durationMillis = 500,
                )
            )

            if (isOnScreen)
                TileUi(
                    painter = { resourceState.value },
                    tileSize = { tileSize },
                    isClickable = { isClickable },
                    offsetX = { animateX },
                    offsetY = { animateY },
                    colorFilter = { colorFilter },
                    onClick = {
                        stepsCounter++

                        isNearestTilePressed = true
                        clickedVector.value = clickedVector.value.copy(
                            newCalculatedVector.first,
                            newCalculatedVector.second
                        )

                        scope.launch {

                            isGameSuspendedNecessarily = true
                            delay(1000)
                            isGameSuspendedNecessarily = false

                            tileObject.changeState(Tile.State.Shown)
                            Log.d("FindPuppyGame", "clicked at: $i $j")
                            selectedTile.bindPosition(i, j)

                            if (type == Tile.Type.WithPuppy) {
                                vibrationCallback(VibrationMode.PuppySpotted)
                                isGameSuspendedNecessarily = true
                                isPuppyFound = true
                                delay(2000)
                                isGameSuspendedNecessarily = false
                                isPuppyFound = false
                                game.generateNewField()
                                stepsCounter = 0
                            }

                            if (tileObject.isEnemy()) {
                                isGameSuspendedNecessarily = true
                                isEnemyFound = true
                                delay(3000)
                                isGameSuspendedNecessarily = false
                                isEnemyFound = false
                                game.generateNewField()
                                stepsCounter = 0
                            }

                            isNearestTilePressed = false

                        }
                    },
                )

        }

    }
}

@SuppressLint("ResourceType")
@Composable
fun PausePopupWindow(
    isPaused: Boolean,
    buttonSize: Dp,
    gapWidth: Dp = 10.dp,
    onReturnButton: () -> Unit,
    onMenuButton: () -> Unit
) {

    Surface(
        color = Color(0x99000000),
        modifier = Modifier
            .zIndex(FindPuppyGame.PAUSE_Z_INDEX)
            .fillMaxSize(if (isPaused) 1f else 0f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.raw.button_play),
                contentDescription = "",
                modifier = Modifier.size(buttonSize)
                    .clip(RoundedCornerShape(99.dp))
                    .clickable(onClick = onReturnButton)
            )

            Spacer(modifier = Modifier.width(gapWidth))

            Image(
                painter = painterResource(id = R.raw.button_home),
                contentDescription = "",
                modifier = Modifier.size(buttonSize)
                    .clip(RoundedCornerShape(99.dp))
                    .clickable(onClick = onMenuButton)
            )
        }
    }

}


@Composable
fun SelectedBorder(
    resourceId: () -> Int,
    tileSize: () -> Dp,
    offsetX: () -> Dp,
    offsetY: () -> Dp
) {
    Image(
        painter = painterResource(id = resourceId()),
        contentDescription = "",
        modifier = Modifier
            .size(tileSize())
            .offset(
                x = offsetX(),
                y = offsetY()
            )
            .zIndex(FindPuppyGame.UI_Z_INDEX)
    )
}

@Composable
fun TileUi(
    painter: () -> Int,
    tileSize: () -> Dp,
    isClickable: () -> Boolean,
    onClick: () -> Unit,
    offsetX: () -> Dp,
    offsetY: () -> Dp,
    colorFilter: () -> ColorFilter?,
) {
    var modifier = Modifier
        .size(tileSize())
        .offset(
            x = offsetX(),
            y = offsetY()
        )

    if (isClickable()) {
        modifier = modifier.clickable { onClick() }
    }

    Image(
        painter = painterResource(id = painter()),
        contentDescription = "",
        contentScale = ContentScale.Fit,
        modifier = modifier,
        colorFilter = colorFilter(),
    )
}

fun isPositionInScreen(
    screenConfig: Configuration,
    vectorPair: Pair<Dp, Dp>,
    additionalRadius: Dp
): Boolean {
    val screenSize = Pair(screenConfig.screenWidthDp.dp, screenConfig.screenHeightDp.dp)
    val isOnScreenHorizontally =
        -additionalRadius <= vectorPair.first && vectorPair.first <= screenSize.first.plus(
            additionalRadius
        )
    val isOnScreenVertically =
        -additionalRadius <= vectorPair.second && vectorPair.second <= screenSize.second.plus(
            additionalRadius
        )
    return isOnScreenHorizontally && isOnScreenVertically
}

@Composable
fun SelectImageByTile(tileObject: Tile, resourceState: MutableState<Int>) {
    val type = tileObject.type.collectAsState(initial = Tile.Type.Neutral).value
    val state = tileObject.state.collectAsState(initial = Tile.State.Hidden).value

    resourceState.value = when (state) {
        Tile.State.Hidden -> R.raw.grass_default_tile

        Tile.State.Shown -> when (type) {
            Tile.Type.Neutral -> R.raw.grass_pressed_tile
            Tile.Type.WithPuppy -> R.raw.puppy
            Tile.Type.WithEnemyMan -> R.raw.enemy_face_man
            Tile.Type.WithEnemyWoman -> R.raw.enemy_face_woman
            Tile.Type.Decoration -> R.raw.trees_tile
            Tile.Type.Dirt -> R.raw.dirt
            Tile.Type.Bush1 -> R.raw.dirt_with_bush_type_1
            Tile.Type.Bush2 -> R.raw.dirt_with_bush_type_2
            Tile.Type.Bush3 -> R.raw.dirt_with_bush_type_3
            Tile.Type.Bush4 -> R.raw.dirt_with_bush_type_4
            Tile.Type.LonelyTree -> R.raw.dirt_with_alone_tree
            Tile.Type.TribeOfTrees -> R.raw.dirt_with_group_of_trees
            else -> R.raw.debug_tile
        }

        else -> R.raw.debug_tile // in case something went wrong just draw this tile
    }
}

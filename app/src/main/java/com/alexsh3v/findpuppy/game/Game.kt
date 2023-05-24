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
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.alexsh3v.findpuppy.FindPuppyGame
import com.alexsh3v.findpuppy.R
import com.alexsh3v.findpuppy.VibrationMode
import com.alexsh3v.findpuppy.ui.theme.AllowToGo
import com.alexsh3v.findpuppy.utils.AudioManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@SuppressLint("ResourceType")
@Composable
fun Game(
    game: FindPuppyGame,
    vibrationCallback: (VibrationMode) -> Unit,
) {

    var isGameJustStarted by remember { mutableStateOf(true) }

    val audioManagerScope = rememberCoroutineScope()

    val audioManager by remember {
        mutableStateOf(
            AudioManager(
                context = game.context,
                scope = audioManagerScope
            )
        )
    }

    val timerScope = rememberCoroutineScope()
    val timer by remember {
        mutableStateOf(Timer(timerScope))
    }
    val timerValue = timer.valueSeconds.collectAsState(initial = 0).value

    val triggerScreamAndVibrationForNearestEnemies: () -> Unit = {
        var isVibrated = false
        for (enemyTile in game.getNearestEnemies()) {

            if (!isVibrated) {
                vibrationCallback(VibrationMode.EnemyNearby)
                isVibrated = true
            }

            if (!game.isChance(FindPuppyGame.CHANCE_OF_ENEMY_SCREAM))
                continue

            audioManager.playRandomAudio(
                type = enemyTile.getType(),
            )
        }
    }

    val generateFieldWithChecksAndVibro: (() -> Unit) -> Unit = {
        it()
        triggerScreamAndVibrationForNearestEnemies()
        timer.start()
    }

    val tileSize by remember {
        mutableStateOf(125.dp)
    }

    remember {
        Log.d(FindPuppyGame.TAG, "INITIAL GENERATION!")
        game.generateNewField(
            showSelectedTile = !isGameJustStarted,
        )
        null
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val scope = rememberCoroutineScope()
    var isGameSuspendedNecessarily by remember {
        mutableStateOf(false)
    }
    var isGameOver by remember {
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
    var selectedResourceId by remember {
        mutableStateOf(R.raw.puppy)
    }
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
    val moveAnimationDurationMillis by remember {
        mutableStateOf(250)
    }
    val selectedBorderX by animateDpAsState(
        targetValue = if (isNearestTilePressed && !isPuppyFound && !isEnemyFound) clickedVector.value.first else centerX,
        animationSpec = tween(
            durationMillis = moveAnimationDurationMillis,
        )
    )
    val selectedBorderY by animateDpAsState(
        targetValue = if (isNearestTilePressed && !isPuppyFound && !isEnemyFound) clickedVector.value.second else centerY,
        animationSpec = tween(
            durationMillis = moveAnimationDurationMillis,
        )
    )

    var borderResource by remember {
        mutableStateOf(R.raw.selected_border)
    }
    val borderResourceDerived by remember {
        derivedStateOf { borderResource }
    }

    if (!isGameJustStarted)
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

    var isResting by remember { mutableStateOf(true) }
    var isTutorial by remember { mutableStateOf(false) }

    if (isResting)
        RestScreen(
            screenType = if (isGameJustStarted) RestScreenType.Login else RestScreenType.Pause,
            onPlayButtonCallback = {
                timer.start()
                audioManager.playUiSound()
                if (isGameJustStarted) {
                    game.getTileAt(selectedTileVector.first, selectedTileVector.second)
                        .changeState(Tile.State.Shown)
                }
                isGameJustStarted = false
                isResting = false
                triggerScreamAndVibrationForNearestEnemies()
            },
            onSettingsButtonCallback = {
                audioManager.playUiSound()
            },
            onRestartButtonCallback = {
                timer.reset()
                audioManager.playUiSound()
                generateFieldWithChecksAndVibro {
                    game.generateNewField(
                        showSelectedTile = true,
                        stayInPosition = selectedTile.getPositionPair()
                    )
                }
                isResting = false
    //            TODO: code restart function
            }
        )

    if (isGameOver)
        FinalScreen(
            title = if (isPuppyFound) stringResource(R.string.game_over_puppy_found)
                else stringResource(R.string.game_over_enemy_found),
            titleColor = if (isPuppyFound) Color.Green else Color.Red,
            stepsCounter = stepsCounter,
            timerValue = timerValue,
            onRestartCallback = {
                timer.reset()
                isGameOver = false
                isPuppyFound = false
                isEnemyFound = false
                generateFieldWithChecksAndVibro {
                    game.generateNewField(
                        showSelectedTile = true,
                        stayInPosition = selectedTile.getPositionPair()
                    )
                }
                stepsCounter = 0
            },
            onEnter = {

            }
        )

    var instructionIndex by remember {
        mutableStateOf(0)
    }

    if (!isResting && !isGameOver)
        StatusBar(
            isTutorial = isTutorial,
            instructionIndex = instructionIndex ,
            stepsCounter = { stepsCounter },
            timePassedInSeconds = timerValue,
            onPauseButtonClick = {
                timer.pause()
                audioManager.playUiSound()
                isPaused = true
                isResting = true
            },
            onQuestionMarkPressed = {
                audioManager.playUiSound()
                instructionIndex = 0
                isResting = false
                isTutorial = true
            },
            onInstructionIndexChanged = {
                audioManager.playUiSound()
                instructionIndex = it
            },
            onExitTutorial = {
                audioManager.playUiSound()
                isTutorial = false
            },
        )

    val triggerPuppy: () -> Unit = {
        Log.d(FindPuppyGame.TAG, "TRIGGER PUPPY RUN!!")
        scope.launch {
            vibrationCallback(VibrationMode.PuppySpotted)
            isGameSuspendedNecessarily = true
            isPuppyFound = true
            timer.pause()

            delay(4000)

            isGameSuspendedNecessarily = false
            isGameOver = true
        }
    }
    val triggerEnemy: () -> Unit = {
        Log.d(FindPuppyGame.TAG, "TRIGGER ENEMY RUN!!")
        scope.launch {
            isGameSuspendedNecessarily = true
            isEnemyFound = true
            timer.pause()

            delay(4000)

            isGameOver = true
            isGameSuspendedNecessarily = false
        }
    }

//    PausePopupWindow(
//        isPaused = isPaused,
//        buttonSize = 100.dp,
//        gapWidth = 25.dp,
//        onReturnButton = {
//            audioManager.playUiSound()
//            isPaused = false
//        },
//        onMenuButton = {
//            audioManager.playUiSound()
//        },
//
//        debugOnPuppy = triggerPuppy,
//        debugOnLost = triggerEnemy
//    )

    for (i in 0 until game.totalFieldSize) {
        for (j in 0 until game.totalFieldSize) {

            val tileObject = game.getTileAt(i, j)
            tileObject.bindPosition(i, j)

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
                    durationMillis = moveAnimationDurationMillis,
                )
            )

            val animateY by animateDpAsState(
                targetValue = newCalculatedVector.second,
                animationSpec = tween(
                    durationMillis = moveAnimationDurationMillis,
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

                        audioManager.playSpecific(
                            resourceId = R.raw.sound_footsteps,
                        )

                        stepsCounter++

                        isNearestTilePressed = true
                        clickedVector.value = clickedVector.value.copy(
                            newCalculatedVector.first,
                            newCalculatedVector.second
                        )

                        scope.launch {

                            isGameSuspendedNecessarily = true
                            delay(moveAnimationDurationMillis * 2L)
                            isGameSuspendedNecessarily = false

                            tileObject.changeState(Tile.State.Shown)
                            Log.d("FindPuppyGame", "clicked at: $i $j")
                            selectedTile.bindPosition(i, j)

                            if (type == Tile.Type.WithPuppy) {
                                audioManager.playRandomAudio(
                                    type = Tile.Type.WithPuppy
                                )
                                triggerPuppy()
                            } else if (tileObject.isEnemy()) {
                                audioManager.playSpecific(
                                    when (type) {
                                        Tile.Type.WithEnemyMan -> R.raw.lose_to_man
                                        Tile.Type.WithEnemyWoman -> R.raw.lose_to_woman
                                        else ->
                                            throw NoSuchFieldException(
                                                "dev forgot to specify enemy sound!"
                                            )
                                    }
                                )
                                triggerEnemy()
                                selectedResourceId = getResourceIdByStateAndType(
                                    state = Tile.State.Shown,
                                    type = type
                                )
                            } else {
                                triggerScreamAndVibrationForNearestEnemies()
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
    onMenuButton: () -> Unit,

    debugOnPuppy: () -> Unit = {},
    debugOnLost: () -> Unit = {},
) {

    var isDebug by remember {
        mutableStateOf(false)
    }


    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth(if (isPaused) 1f else 0f)
            .zIndex(FindPuppyGame.PAUSE_Z_INDEX + 1)
    ) {
        Box(
            modifier = Modifier
//                .background(Color(0xffff0000))
                .alpha(0f)
                .size(40.dp)
                .clickable {
                    isDebug = !isDebug
                }
        ) {

        }
    }

    Surface(
        color = Color(0x99000000),
        modifier = Modifier
            .zIndex(FindPuppyGame.PAUSE_Z_INDEX)
            .fillMaxSize(if (isPaused) 1f else 0f)
    ) {

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
//                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.raw.button_play),
                    contentDescription = "",
                    modifier = Modifier
                        .size(buttonSize)
                        .clip(RoundedCornerShape(99.dp))
                        .clickable(onClick = onReturnButton)
                )

                Spacer(modifier = Modifier.width(gapWidth))

                Image(
                    painter = painterResource(id = R.raw.button_home),
                    contentDescription = "",
                    modifier = Modifier
                        .size(buttonSize)
                        .clip(RoundedCornerShape(99.dp))
                        .clickable(onClick = onMenuButton)
                )
            }

            // DEBUG MENU
            if (isDebug)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {

                    Button(onClick = {
                        onReturnButton()
                        debugOnPuppy()
                    }) {
                        Text(text = "debug:WIN")
                    }

                    Button(onClick = {
                        onReturnButton()
                        debugOnLost()
                    }) {
                        Text(text = "debug:LOST")
                    }

                }
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
            .zIndex(FindPuppyGame.UI_SCREEN_Z_INDEX)
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

    resourceState.value = getResourceIdByStateAndType(state, type)
}

fun getResourceIdByStateAndType(state: Tile.State, type: Tile.Type): Int {
    return when (state) {
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

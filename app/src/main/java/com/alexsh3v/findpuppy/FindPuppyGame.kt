package com.alexsh3v.findpuppy

import android.content.Context
import android.util.Log
import com.alexsh3v.findpuppy.game.Tile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random

class FindPuppyGame(val context: Context) {

    enum class ScreenType {
        Menu, Game
    }

    companion object {
        private const val FIELD_SIZE = 8
        private const val OUT_FIELD_LAYER_NUMBER = 5
        const val TAG = "FindPuppyGame"
        const val REST_SCREEN_Z_INDEX = 100f
        const val PAUSE_Z_INDEX = 20f
        const val UI_SCREEN_Z_INDEX = 10f
        const val LOADING_SCREEN_Z_INDEX = 100f

        private const val CHANCE_OF_DECORATION_PERCENT = 20//%
        const val CHANCE_OF_ENEMY_SCREAM = 100//%
    }

    var fieldSizePair: Pair<Int, Int> = Pair(2, 2)

    // TODO: CHANGE TO "MENU"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //       ------------------------------------vvvv
    var screenType = MutableStateFlow(ScreenType.Game)
    var listOfTiles = MutableStateFlow(ArrayList<ArrayList<Tile>>())
    var selectedTile = MutableStateFlow(Tile())

    val totalFieldSize: Int
        get() = FIELD_SIZE + OUT_FIELD_LAYER_NUMBER * 2

    fun generateNewField(showSelectedTile: Boolean, stayInPosition: Pair<Int, Int>? = null) {
        // FIXME: code probably will break after scaling width and height
        //        of "listOfTiles" array, because of how I wrote it :)

        /*
        Field representation:
                      i
         ----------------->
        | 0 0 0 0 0 0 0 .
        | 0 0 0 0 0 0 0 .
        | 0 0 0 0 0 0 0 .
     j  | . . . . . . . .
        V
         */

        // Create field "totalFieldSize" x "totalFieldSize"
        // Fill with random content in tile
        if (listOfTiles.value.size == 0)
            for (i in 0 until totalFieldSize) {
                val newList = ArrayList<Tile>()
                listOfTiles.value.add(newList)
                for (j in 0 until totalFieldSize) {
                    newList.add(
                        when {
                            isInField(i, j) -> Tile(Tile.Type.Neutral).apply {
                                changeState(Tile.State.Hidden)
                            }
                            else -> Tile(getRandomDecorationType()).apply {
                                changeState(Tile.State.Shown)
                            }
                        }
                    )
                }
            }
        else
            for (i in 0 until totalFieldSize) {
                for ((j, tile) in listOfTiles.value[i].withIndex()) {
                    when {
                        isInField(i, j) -> {
                            tile.changeType(Tile.Type.Neutral)
                            tile.changeState(Tile.State.Hidden)
                        }
                        else -> {
//                            tile.changeType(getRandomDecorationType())
//                            tile.changeState(Tile.State.Shown)
                        }
                    }
                }
            }

        val stayPositionX = stayInPosition?.second ?: -1
        val stayPositionY = stayInPosition?.first ?: -1

        // Place Puppy
        val fieldRange = Pair(
            OUT_FIELD_LAYER_NUMBER, totalFieldSize - OUT_FIELD_LAYER_NUMBER
        )
        var emptyTile: Tile
        var randomX: Int = -1
        var randomY: Int = -1

        fun checkIsInStayingPosition(): Boolean {
            return randomY == stayPositionY && randomX == stayPositionX
        }

        do {
            randomX = Random.nextInt(fieldRange.first, fieldRange.second)
            randomY = Random.nextInt(fieldRange.first, fieldRange.second)
            emptyTile = listOfTiles.value[randomY][randomX]
        } while (!emptyTile.isEmpty() || checkIsInStayingPosition())

        listOfTiles.value[randomY][randomX].changeType(Tile.Type.WithPuppy)

        // TODO: refactor repeating code
        for (i in 0..6) {
            do {
                randomX = Random.nextInt(fieldRange.first, fieldRange.second)
                randomY = Random.nextInt(fieldRange.first, fieldRange.second)
                emptyTile = listOfTiles.value[randomY][randomX]
            } while (!emptyTile.isEmpty() || checkIsInStayingPosition())
            getTileAt(randomY, randomX).changeType(getRandomEnemy())
        }

        do {

            if (stayInPosition != null) {
                randomX = stayPositionX
                randomY = stayPositionY
                break
            }

            randomX = Random.nextInt(fieldRange.first, fieldRange.second)
            randomY = Random.nextInt(fieldRange.first, fieldRange.second)
            emptyTile = listOfTiles.value[randomY][randomX]
        } while (!emptyTile.isEmpty() || checkIsInStayingPosition())

        selectedTile.value.bindPosition(randomY, randomX)
        if (showSelectedTile)
            listOfTiles.value[randomY][randomX].changeState(Tile.State.Shown)

    }

    private fun getRandomDecorationType(): Tile.Type {

        val isChanceSucceed = Random.nextInt(0, 101) >= (100 - CHANCE_OF_DECORATION_PERCENT)

        if (!isChanceSucceed)
            return Tile.Type.Dirt

        return listOf(
            Tile.Type.LonelyTree,
            Tile.Type.TribeOfTrees,
            Tile.Type.Bush1,
            Tile.Type.Bush2,
            Tile.Type.Bush3,
            Tile.Type.Bush4,
        )[Random.nextInt(0, Tile.DECORATION_NUMBER)]
    }

    private fun getRandomEnemy(): Tile.Type {
        return listOf(
            Tile.Type.WithEnemyMan,
            Tile.Type.WithEnemyWoman
        )[Random.nextInt(Tile.ENEMY_NUMBER)]
    }

    private fun isInField(i: Int, j: Int): Boolean {
        return (OUT_FIELD_LAYER_NUMBER <= i && i <= OUT_FIELD_LAYER_NUMBER + FIELD_SIZE - 1)
                && (OUT_FIELD_LAYER_NUMBER <= j && j <= OUT_FIELD_LAYER_NUMBER + FIELD_SIZE - 1)
    }

    fun getTileAt(i: Int, j: Int): Tile {

        if (i !in 0 until totalFieldSize || j !in 0 until totalFieldSize)
            throw IndexOutOfBoundsException("got unexpected position: ($i, $j)")

        return listOfTiles.value[i][j]
    }

    fun debugLogFieldWithStates(pos: Pair<Int, Int>? = null) {

        // Debug info
        if (pos == null)
            Log.d(TAG, "[[[ CREATED LIST ]]]")
        else
            Log.d(TAG, "[[[ MOVED IN THE LIST ]]]")

        Log.d(TAG, "TOTAL: $totalFieldSize")

        var tile: Tile
        var s = ""
        for (i in 0 until totalFieldSize) {
            for (j in 0 until totalFieldSize) {
                tile = getTileAt(i, j)
                var element = if (tile.isDecoration()) "." else "N"
                if (pos != null) {
                    element = if (pos.first == i && pos.second == j)
                        "<$element>"
                    else
                        " $element "
                }
                s += "$element "
            }
            s += "\n"
        }

        Log.d(TAG, s)
    }

    fun isPositionNearSelected(
        positionInArray: Pair<Int, Int>,
        additionalRadius: Int = 10
    ): Boolean {
        val i = positionInArray.first
        val j = positionInArray.second
        val selectedPair = selectedTile.value.getPositionPair()

        val isInVertical =
            (selectedPair.first - additionalRadius <= i && i <= selectedPair.first + additionalRadius)
        val isInHorizontal =
            (selectedPair.second - additionalRadius <= j && j <= selectedPair.second + additionalRadius)

        return isInHorizontal && isInVertical

    }

    fun getNearestEnemies(): ArrayList<Tile> {
        val position = selectedTile.value.getPositionPair()
        val leftTop = Pair(
            position.first - 1,
            position.second - 1
        )
        val rightBottom = Pair(
            position.first + 1,
            position.second + 1
        )
        val enemiesList = ArrayList<Tile>()

        for (i in leftTop.first .. rightBottom.first) {
            for (j in leftTop.second .. rightBottom.second) {

                if (!isInField(i, j))
                    continue

                val tile = getTileAt(i, j)

                if (!tile.isEnemy())
                    continue

                enemiesList.add(tile)

            }
        }

        return enemiesList

    }

    fun isChance(chancePercent: Int): Boolean {
        val isChanceSucceed = Random.nextInt(0, 101) >= (100 - chancePercent)
        return isChanceSucceed
    }
}
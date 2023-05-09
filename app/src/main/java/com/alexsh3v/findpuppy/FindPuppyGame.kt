package com.alexsh3v.findpuppy

import android.util.Log
import com.alexsh3v.findpuppy.game.Tile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random

class FindPuppyGame {

    enum class ScreenType {
        Menu, Game
    }

    companion object {
        private const val FIELD_SIZE = 8
        private const val OUT_FIELD_LAYER_NUMBER = 5
        const val TAG = "FindPuppyGame"
        private const val CHANCE_OF_DECORATION_PERCENT = 20
    }

    // TODO: CHANGE TO "MENU"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //       ------------------------------------vvvv
    var screenType = MutableStateFlow(ScreenType.Game)
    var listOfTiles = MutableStateFlow(ArrayList<ArrayList<Tile>>())
    var selectedTile = MutableStateFlow(Tile())

    val totalFieldSize: Int
        get() = FIELD_SIZE + OUT_FIELD_LAYER_NUMBER * 2

    fun generateNewField() {
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
                            tile.changeType(getRandomDecorationType())
                            tile.changeState(Tile.State.Shown)
                        }
                    }
                }
            }

        // Place Puppy
        val fieldRange = Pair(
            OUT_FIELD_LAYER_NUMBER, totalFieldSize - OUT_FIELD_LAYER_NUMBER
        )
        var randomX = Random.nextInt(fieldRange.first, fieldRange.second)
        var randomY = Random.nextInt(fieldRange.first, fieldRange.second)

        listOfTiles.value[randomY][randomX].changeType(Tile.Type.WithPuppy)

        // TODO: refactor repeating code
        for (i in 0 .. 6) {
            var emptyTile: Tile
            do {
                randomX = Random.nextInt(fieldRange.first, fieldRange.second)
                randomY = Random.nextInt(fieldRange.first, fieldRange.second)
                emptyTile = listOfTiles.value[randomY][randomX]
            } while (!emptyTile.isEmpty())
            getTileAt(randomY, randomX).changeType(getRandomEnemy())
        }

        var emptyTile: Tile
        do {
            randomX = Random.nextInt(fieldRange.first, fieldRange.second)
            randomY = Random.nextInt(fieldRange.first, fieldRange.second)
            emptyTile = listOfTiles.value[randomY][randomX]
        } while (!emptyTile.isEmpty())

        selectedTile.value.bindPosition(randomY, randomX)
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
}
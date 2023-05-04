package com.alexsh3v.findpuppy

import com.alexsh3v.findpuppy.game.Tile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random

class FindPuppyGame {

    enum class ScreenType {
        Menu, Game
    }

    companion object {
        const val FIELD_SIZE = 8
    }

    // TODO: CHANGE TO "MENU"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //       ------------------------------------vvvv
    var screenType = MutableStateFlow(ScreenType.Game)
    var listOfTiles = MutableStateFlow(ArrayList<ArrayList<Tile>>())
    var selectedTile = MutableStateFlow(Tile())

    fun generateNewField() {
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

        // Create field FIELD_SIZE x FIELD_SIZE
        // Fill with random content in tile
        if (listOfTiles.value.size == 0)
            for (i in 0 until FIELD_SIZE) {
                val newList = ArrayList<Tile>()
                listOfTiles.value.add(newList)
                for (j in 0 until FIELD_SIZE) {
                    newList.add(Tile(Tile.Type.Neutral))
                }
            }
        else
            for (i in 0 until FIELD_SIZE) {
                for (tile in listOfTiles.value[i]) {
                    tile.changeType(Tile.Type.Neutral)
                    tile.changeState(Tile.State.Hidden)
                }
            }

        // Place Puppy
        var randomX = Random.nextInt(0, FIELD_SIZE - 1)
        var randomY = Random.nextInt(0, FIELD_SIZE - 1)
        listOfTiles.value[randomY][randomX].changeType(Tile.Type.WithPuppy)

        var emptyTile: Tile
        do {
            randomX = Random.nextInt(0, FIELD_SIZE - 1)
            randomY = Random.nextInt(0, FIELD_SIZE - 1)
            emptyTile = listOfTiles.value[randomY][randomX]
        } while (!emptyTile.isEmpty())

        // FIXME: MAKE SURE THIS IS NOT CORRUPTED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        selectedTile.value.bindPosition(randomY, randomX)
        listOfTiles.value[randomY][randomX].changeState(Tile.State.Shown)
    }

    fun getTileAt(i: Int, j: Int): Tile {

        if (i !in 0 until FIELD_SIZE || j !in 0 until FIELD_SIZE)
            throw IndexOutOfBoundsException("got unexpected position: ($i, $j)")

        return listOfTiles.value[i][j]
    }

}
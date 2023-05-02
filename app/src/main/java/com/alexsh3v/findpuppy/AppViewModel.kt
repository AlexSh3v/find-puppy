package com.alexsh3v.findpuppy

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.alexsh3v.findpuppy.game.Cell
import kotlin.random.Random

class AppViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    enum class ScreenType {
        Menu, Game
    }

    companion object {
        const val FIELD_SIZE = 8
    }

    // TODO: CHANGE TO "MENU"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //       -------------------------------------------------------------------vvvv
    var screenType = savedStateHandle.getStateFlow<ScreenType>("screenType", ScreenType.Game)
    var listOfCells =
        savedStateHandle.getStateFlow<ArrayList<ArrayList<Cell>>>("listOfCells", ArrayList())

    var selectedCell = savedStateHandle.getStateFlow("selectedCell", Cell())


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
        // Fill with random content in cell
        if (listOfCells.value.size == 0)
            for (i in 0 until FIELD_SIZE) {
                val newList = ArrayList<Cell>()
                listOfCells.value.add(newList)
                for (j in 0 until FIELD_SIZE) {
                    newList.add(Cell(Cell.Type.Neutral))
                }
            }
        else
            for (i in 0 until FIELD_SIZE) {
                for (cell in listOfCells.value[i]) {
                    cell.changeType(Cell.Type.Neutral)
                    cell.changeState(Cell.State.Hidden)
                }
            }

        // Place Puppy
        var randomX = Random.nextInt(0, FIELD_SIZE - 1)
        var randomY = Random.nextInt(0, FIELD_SIZE - 1)
        listOfCells.value[randomY][randomX].changeType(Cell.Type.WithPuppy)

        var emptyCell: Cell
        do {
            randomX = Random.nextInt(0, FIELD_SIZE - 1)
            randomY = Random.nextInt(0, FIELD_SIZE - 1)
            emptyCell = listOfCells.value[randomY][randomX]
        } while (!emptyCell.isEmpty())

        // FIXME: MAKE SURE THIS IS NOT CORRUPTED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        selectedCell.value.bindPosition(randomX, randomY)
        listOfCells.value[randomY][randomX].changeState(Cell.State.Shown)
    }

    fun getCellAt(i: Int, j: Int): Cell {

        if (i !in 0 until FIELD_SIZE || j !in 0 until FIELD_SIZE)
            throw IndexOutOfBoundsException("got unexpected position: ($i, $j)")

        return listOfCells.value[i][j]
    }

}
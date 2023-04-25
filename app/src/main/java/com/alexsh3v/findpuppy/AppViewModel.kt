package com.alexsh3v.findpuppy

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.alexsh3v.findpuppy.game.Cell
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppViewModel(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    enum class ScreenType {
        Menu, Game
    }

    // TODO: CHANGE TO "MENU"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //       -------------------------------------------------------------------vvvv
    var screenType = savedStateHandle.getStateFlow<ScreenType>("screenType", ScreenType.Game)
    var listOfCells = savedStateHandle.getStateFlow<ArrayList<Cell>>("listOfCells", ArrayList())


    fun generateNewField() {

    }

}
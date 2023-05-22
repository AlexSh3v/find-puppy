package com.alexsh3v.findpuppy.game

sealed class RestScreenType {
    object Login : RestScreenType()
    object Pause : RestScreenType()
}

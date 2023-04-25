package com.alexsh3v.findpuppy.game

class Cell(var type: Type) {
    enum class Type {
        Neutral, WithPuppy, WithEnemy, WithItems
    }

}

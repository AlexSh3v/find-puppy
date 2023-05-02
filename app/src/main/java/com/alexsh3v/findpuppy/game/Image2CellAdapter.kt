package com.alexsh3v.findpuppy.game

import com.alexsh3v.findpuppy.R

class Image2CellAdapter private constructor() {
    companion object {
        fun adapt(resourceId: Int): Cell.Type {
            return when (resourceId) {
                // TODO: make [texture -> Cell.Type]
                R.raw.cell_static -> Cell.Type.Neutral
                // ...
                else -> Cell.Type.Neutral
            }
        }
    }
}
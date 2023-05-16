package com.alexsh3v.findpuppy.game

import androidx.compose.ui.graphics.Color
import com.alexsh3v.findpuppy.R

interface LoadingScreenEvents {
    fun load(durationMillis: Int)
    fun changeMob(
        resourceId: Int = R.raw.puppy,
        backgroundColor: Color = Color.Green
    )
}
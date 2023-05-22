package com.alexsh3v.findpuppy.utils

import android.content.res.Configuration
import androidx.compose.ui.unit.dp
import java.lang.Math.ceil
import kotlin.math.ceil

class ScreenConfiguration(
    private val configuration: Configuration
) {

    companion object {
        const val SAFETY_ZONE_SIZE = 2
    }

    val screenHeight
        get() = configuration.screenHeightDp.dp
    val screenWidth
        get() = configuration.screenWidthDp.dp

    fun calculateFieldSize(tileSize: Float): Pair<Int, Int> {

        val max_j = ceil(screenWidth.value / tileSize) + SAFETY_ZONE_SIZE
        val max_i = ceil(screenHeight.value / tileSize) + SAFETY_ZONE_SIZE

        return Pair(
            max_i.toInt(),
            max_j.toInt()
        )

    }

}
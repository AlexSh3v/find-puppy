package com.alexsh3v.findpuppy.game

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.alexsh3v.findpuppy.FindPuppyGame
import com.alexsh3v.findpuppy.R

@Composable
fun LoadingScreen(
    isActivated: Boolean,
    resourceId: Int,
    backgroundColor: Color,
    animationDurationMillis: Long = 500,
) {

    val alphaAnimation by animateFloatAsState(
        targetValue = if (isActivated) 1f else 0f,
        animationSpec = tween(
            durationMillis = animationDurationMillis.toInt(),
            easing = LinearEasing
        )
    )

    Log.d(FindPuppyGame.TAG, "ALPHA: $alphaAnimation")

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .alpha(alphaAnimation)
            .background(backgroundColor)
            .zIndex(FindPuppyGame.LOADING_SCREEN_Z_INDEX)
    ) {
        Image(
            painter = painterResource(id = resourceId),
            contentDescription = "",
            modifier = Modifier.size(100.dp)
        )

    }

}

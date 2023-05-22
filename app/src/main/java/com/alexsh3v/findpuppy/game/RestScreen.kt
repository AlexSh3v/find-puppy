package com.alexsh3v.findpuppy.game

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.alexsh3v.findpuppy.FindPuppyGame
import com.alexsh3v.findpuppy.R

@SuppressLint("ResourceType")
@Composable
fun RestScreen(
    screenType: RestScreenType,
    onPlayButtonCallback: () -> Unit,
    onSettingsButtonCallback: () -> Unit,
    onRestartButtonCallback: () -> Unit,
) {

    val playButtonSize by remember { mutableStateOf(100.dp) }
    val smallerButtonSize by remember { mutableStateOf(75.dp) }

    Surface(
        color = Color(0x99000000),
        modifier = Modifier
            .fillMaxSize(1f)
            .zIndex(FindPuppyGame.REST_SCREEN_Z_INDEX),
    ) {

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(1f)
        ) {

            Image(
                painter = painterResource(id = R.raw.button_play),
                contentDescription = null,
                modifier = Modifier
                    .size(playButtonSize)
                    .clip(RoundedCornerShape(99.dp))
                    .clickable(onClick = onPlayButtonCallback)
            )

            if (screenType is RestScreenType.Pause)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {

                    Image(
                        painter = painterResource(id = R.raw.restart_button),
                        contentDescription = null,
                        modifier = Modifier
                            .size(smallerButtonSize)
                            .clip(RoundedCornerShape(99.dp))
                            .clickable(onClick = onRestartButtonCallback)
                    )

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Image(
                        painter = painterResource(id = R.raw.settings_button),
                        contentDescription = null,
                        modifier = Modifier
                            .size(smallerButtonSize)
                            .clip(RoundedCornerShape(99.dp))
                            .clickable(onClick = onSettingsButtonCallback)
                    )

                }

        }
    }

}
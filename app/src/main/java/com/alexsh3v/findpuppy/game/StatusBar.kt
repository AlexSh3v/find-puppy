package com.alexsh3v.findpuppy.game

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.alexsh3v.findpuppy.FindPuppyGame
import com.alexsh3v.findpuppy.R
import com.alexsh3v.findpuppy.ui.theme.PuppyMain


@SuppressLint("ResourceType")
@Composable
fun StatusBar(
    stepsCounter: () -> Int,
    timePassedInSeconds: () -> Int,
    onPauseButtonClick: () -> Unit,
    pauseButtonSize: Dp = 65.dp,
    metricsWidth: Dp = 100.dp,
    fontSize: TextUnit = 28.sp,
    iconSize: Dp = 40.dp,
    isDebug: Boolean = false
) {
    val barSize by remember {
        mutableStateOf(150.dp)
    }
    val metricsModifier = Modifier
        .width(metricsWidth)
        .run {
            if (isDebug) this.border(1.dp, Color.Black)
            else this
        }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(barSize)
            .zIndex(FindPuppyGame.UI_Z_INDEX)
            .run {
                if (isDebug) this.border(1.dp, Color.Black)
                else this
            },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {

        // Pause Button
        Image(
            painter = painterResource(id = R.raw.ico_pause),
            contentDescription = "",
            modifier = Modifier
                .padding(10.dp)
                .clip(CircleShape.copy(CornerSize(99.dp)))
                .size(pauseButtonSize)
                .clickable { onPauseButtonClick() },
            contentScale = ContentScale.Fit,
        )

        // Interface: Step Number & Timer
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(
                    vertical = 10.dp,
                    horizontal = 20.dp
                )
                .background(
                    color = Color(0x77000000),
                    shape = RoundedCornerShape(10.dp)
                )
                .fillMaxHeight(.5f)
//                .fillMaxHeight() // TODO: probably uncomment in the future
        ) {

            // STEPS
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = metricsModifier
//                    .weight(1f), // TODO: uncomment
            ) {

                Text(
                    text = stepsCounter().toString(),
                    color = PuppyMain,
                    fontSize = fontSize,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.weight(3f),
                )

                Spacer(modifier = Modifier.width(5.dp))

                Image(
                    painter = painterResource(id = R.raw.ico_footsteps),
                    contentDescription = "",
                    modifier = Modifier
                        .weight(1f)
                        .size(iconSize),
                    contentScale = ContentScale.Fit
                )

            }

//            // TIMER:
//            // TODO: add functional timer
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceEvenly,
//                modifier = metricsModifier
//                    .weight(1f),
//            ) {
//
//                Text(
//                    text = timePassedInSeconds().toString(),
//                    color = PuppyMain,
//                    fontSize = fontSize,
//                    textAlign = TextAlign.Right,
//                    modifier = Modifier.weight(3f)
//                )
//
//                Spacer(modifier = Modifier.width(5.dp))
//
//                Image(
//                    painter = painterResource(id = R.raw.ico_timer),
//                    contentDescription = "",
//                    modifier = Modifier
//                        .weight(1f)
//                        .size(iconSize),
//                    contentScale = ContentScale.Fit
//                )
//            }

        }
    }
}

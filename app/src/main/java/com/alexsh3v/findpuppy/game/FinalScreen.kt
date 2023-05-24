package com.alexsh3v.findpuppy.game

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.alexsh3v.findpuppy.FindPuppyGame
import com.alexsh3v.findpuppy.R
import com.alexsh3v.findpuppy.ui.theme.PuppyMain

@SuppressLint("ResourceType")
@Composable
fun FinalScreen(
    title: String,
    titleColor: Color,
    stepsCounter: Int,
    timerValue: Int,
    onRestartCallback: () -> Unit,
    onEnter: () -> Unit
) {

    val iconSize = 50.dp
    val textSize = 26.sp

    Surface(
        color = Color(0xB1000000),
        modifier = Modifier
            .fillMaxSize(1f)
            .zIndex(FindPuppyGame.REST_SCREEN_Z_INDEX),
    ) {

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {

            Text(
                text = title,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column() {

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(.3f)
                ) {

                    Image(
                        painter = painterResource(id = R.raw.ico_timer),
                        contentDescription = "",
                        modifier = Modifier
                            .size(iconSize),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        Timer.prettify(timerValue),
                        color = PuppyMain,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .weight(3f)
                    )

                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(.3f)
                ) {

                    Image(
                        painter = painterResource(id = R.raw.ico_footsteps),
                        contentDescription = "",
                        modifier = Modifier
                            .size(iconSize),
                        contentScale = ContentScale.Fit
                )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        stepsCounter.toString(),
                        color = PuppyMain,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .weight(3f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))

            Image(
                painter = painterResource(id = R.raw.restart_button),
                contentDescription = null,
                modifier = Modifier
                    .size(iconSize.plus(20.dp))
                    .clip(RoundedCornerShape(99.dp))
                    .clickable(onClick = onRestartCallback)
            )


        }

    }

}
package com.alexsh3v.findpuppy.game

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.alexsh3v.findpuppy.FindPuppyGame
import com.alexsh3v.findpuppy.R
import com.alexsh3v.findpuppy.ui.theme.DarkTransparent
import java.lang.Integer.max

@SuppressLint("ResourceType")
@Composable
fun HowToPlayScreen(
    instructionIndex: Int,
    onInstructionIndexChanged: (Int) -> Unit,
    onExitTutorial: () -> Unit,
    buttonSize: Dp = 60.dp,
) {

    val helpInstructions = stringArrayResource(id = R.array.help_instructions)

    Surface(
        color = DarkTransparent.copy(alpha = .2f),
        modifier = Modifier
            .zIndex(FindPuppyGame.UI_SCREEN_Z_INDEX + 1)
            .height(175.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp)
        ) {

//            Image(
//                painter = painterResource(id = R.raw.helper_anastasia),
//                contentDescription = null,
//                modifier = Modifier
//                    .size(150.dp)
//                    .clip(RoundedCornerShape(99.dp))
//                    .border(5.dp, Color.Green, RoundedCornerShape(99.dp))
//                    .weight(1f)
//            )

            Column(
                modifier = Modifier
                    .weight(2f)
            ) {

                Column(
                    modifier = Modifier.weight(2f)
                ) {

                    Text(
                        text = stringResource(id = R.string.tutorial),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = helpInstructions[instructionIndex],
                        color = Color.White,
                        modifier = Modifier.weight(3f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.weight(1f)
                ) {

                    Image(
                        painter = painterResource(id = R.raw.question_button),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .clip(CircleShape.copy(CornerSize(99.dp)))
                            .size(buttonSize)
                            .clickable { onExitTutorial() },
                    )

                    val isLeftArrowEnabled = instructionIndex > 0
                    Image(
                        painter = painterResource(id = R.raw.arrow_left_button),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        colorFilter = if (!isLeftArrowEnabled)
                            ColorFilter.tint(Color(0f, 0f, 0f, .3f), BlendMode.Darken)
                        else null,
                        modifier = Modifier
                            .clip(CircleShape.copy(CornerSize(99.dp)))
                            .size(buttonSize)
                            .clickable(
                                enabled = isLeftArrowEnabled,
                                onClick = {
                                    val index = max(instructionIndex - 1, 0)
                                    onInstructionIndexChanged(index)
                                }
                            )
                    )

                    val isRightArrowEnabled = (instructionIndex + 1 < helpInstructions.size)
                    Image(
                        painter = painterResource(id = R.raw.arrow_right_button),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        colorFilter = if (!isRightArrowEnabled)
                            ColorFilter.tint(Color(0f, 0f, 0f, .3f), BlendMode.Darken)
                        else null,
                        modifier = Modifier
                            .clip(CircleShape.copy(CornerSize(99.dp)))
                            .size(buttonSize)
                            .clickable(
                                enabled = isRightArrowEnabled,
                                onClick = {
                                    val index = (instructionIndex + 1) % helpInstructions.size
                                    onInstructionIndexChanged(index)
                                }
                            )
                    )

                }
            }


        }
    }

}
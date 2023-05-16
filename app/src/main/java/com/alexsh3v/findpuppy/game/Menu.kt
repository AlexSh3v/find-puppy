package com.alexsh3v.findpuppy.game

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.alexsh3v.findpuppy.FindPuppyGame
import com.alexsh3v.findpuppy.R
import com.alexsh3v.findpuppy.VibrationMode
import com.alexsh3v.findpuppy.utils.Screen

@SuppressLint("ResourceType")
@Composable
fun Menu(
    game: FindPuppyGame,
    vibrationCallback: (VibrationMode) -> Unit,
    navController: NavHostController
) {

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue)
    ) {
        Image(
            painter = painterResource(id = R.raw.menu_2_0),
            contentDescription = "",
            contentScale = ContentScale.Crop,
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.raw.button_play),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(100.dp)
                    .offset(y = 120.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .clickable {
                        navController.navigate(Screen.Game.route)
                    }
            )
        }

    }

}


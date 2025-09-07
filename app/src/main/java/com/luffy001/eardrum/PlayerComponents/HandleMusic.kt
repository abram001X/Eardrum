package com.luffy001.eardrum.PlayerComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.luffy001.eardrum.R
import com.luffy001.eardrum.lib.playerController

@Composable
fun HandleMusic() {
    val tint = Color.White
    val leftImage = painterResource(R.drawable.ic_left_arrow)
    val playImage = painterResource(R.drawable.ic_play)
    val rightImage = painterResource(R.drawable.ic_right_arrow)
    val pauseImage = painterResource(R.drawable.ic_pause)
    val randomImage = painterResource(R.drawable.ic_random)

    Icon(
        randomImage, "random", Modifier
            .clickable {
                changePLayer()
            }
            .padding(start = 10.dp)
            .size(30.dp), tint
    )
    Icon(
        leftImage, "Left", Modifier
            .clickable {
                playerController.previousNextAudio(false)
            }
            .size(140.dp), tint
    )

    Icon(
        painter = if (playerController.isPlaying) pauseImage else playImage,
        contentDescription = "PauseOrPlay",
        Modifier
            .size(100.dp)
            .clickable(onClick = {
                playerController.playAndStop()
            }),
        tint
    )

    Icon(
        rightImage,
        "Right",
        Modifier
            .size(140.dp)
            .clickable(onClick = { playerController.previousNextAudio(true) }),
        tint
    )


}

fun changePLayer(){
    playerController.activeRandomMode()
}
package com.luffy001.eardrum.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luffy001.eardrum.lib.imageFromPath
import com.luffy001.eardrum.R
import com.luffy001.eardrum.lib.playerController
import java.util.concurrent.TimeUnit

var audioFile by mutableStateOf(playerController.audioPlaying)

@Composable
fun InitPlayerApp() {
    audioFile = playerController.audioPlaying
    PlayerApp()
}

@Composable
fun ImagePlayer() {
    val modifier = Modifier
        .height(400.dp)
        .width(350.dp)
        .padding(10.dp)
    val imageMusic = painterResource(R.drawable.ic_logosimple)
    val image = imageFromPath(audioFile.contentUri)
    if (image !== null) {
        Image(bitmap = image, contentDescription = audioFile.name, modifier)
    } else Image(painter = imageMusic, contentDescription = audioFile.name, modifier)
}

@Composable
fun PlayerApp() {
    if (playerController.isPlaying) playerController.runAudio()
    val tint = Color.White
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImagePlayer()
        Spacer(Modifier.height(50.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .height(22.dp), Arrangement.Center, Alignment.CenterVertically
        ) {
            Text(text = audioFile.name, color = tint, fontSize = 20.sp)
        }
        SliderM3()
        VisPosition()
        Row(
            Modifier
                .fillMaxWidth()
                .height(200.dp), Arrangement.Center, Alignment.CenterVertically
        ) {
            HandleMusic()
        }
    }
}

@SuppressLint("DefaultLocale")
fun msToTime(ms: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ms)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
    return String.format("%d:%02d", minutes, seconds)
}

@Composable
fun VisPosition() {
    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.SpaceBetween) {

        Text(
            fontSize = 16.sp,
            text = msToTime(playerController.currentPosition.toLong()),
            color = Color.White
        )

        Text(
            fontSize = 16.sp,
            text = msToTime(playerController.player.duration),
            color = Color.White,
        )


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderM3() {
    Spacer(Modifier.height(40.dp))
    Slider(
        modifier = Modifier.fillMaxWidth(),
        value = playerController.currentPosition,
        onValueChange = {
            playerController.setPosition(it.toLong())
            Log.i("pos", it.toString())
        },
        valueRange = 0f..audioFile.duration.toFloat(),
        thumb = {
            Box(
                modifier = Modifier
                    .padding(0.dp)
                    .size(32.dp)
                    .background(
                        color = Color.White, shape = CircleShape
                    )
            )
        })
}

@Composable
fun HandleMusic() {
    val tint = Color.White
    val leftImage = painterResource(R.drawable.ic_left_arrow)
    val playImage = painterResource(R.drawable.ic_play)
    val rightImage = painterResource(R.drawable.ic_right_arrow)
    val pauseImage = painterResource(R.drawable.ic_pause)
    Icon(
        leftImage, "Left", Modifier
            .clickable {
                playerController.previousNextAudio(false)
            }
            .size(140.dp), tint)

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

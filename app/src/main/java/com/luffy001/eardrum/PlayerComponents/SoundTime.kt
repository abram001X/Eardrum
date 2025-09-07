package com.luffy001.eardrum.PlayerComponents

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luffy001.eardrum.lib.playerController
import com.luffy001.eardrum.screens.audioFile
import java.util.concurrent.TimeUnit


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
            text = msToTime(audioFile.duration.toLong()),
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



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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luffy001.eardrum.service.PlaybackViewModel
import java.util.concurrent.TimeUnit


@SuppressLint("DefaultLocale")
fun msToTime(ms: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ms)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
    return String.format("%d:%02d", minutes, seconds)
}

@Composable
fun VisPosition(viewModel: PlaybackViewModel) {

    val currentPosition by viewModel.currentPosition.observeAsState(0f)
    val audioPlaying by viewModel.audioPlaying.observeAsState(null)
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            fontSize = 16.sp,
            text = msToTime(currentPosition.toLong()),
            color = Color.White
        )

        Text(
            fontSize = 16.sp,
            text = msToTime(audioPlaying?.duration?.toLong() ?: 0L),
            color = Color.White,
        )


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderM3(viewModel: PlaybackViewModel) {
    val isPlaying by viewModel.isPlaying.observeAsState(false)
    val currentPosition by viewModel.currentPosition.observeAsState(0f)
    val audioPlaying by viewModel.audioPlaying.observeAsState(null)
    var duration by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(isPlaying) {
        if (isPlaying) viewModel.runAudio()
    }
    LaunchedEffect(audioPlaying) {
        duration = audioPlaying?.duration?.toFloat() ?: 0f
    }
    Spacer(Modifier.height(40.dp))
    Slider(
        modifier = Modifier.fillMaxWidth(),
        value = currentPosition,
        onValueChange = {
            viewModel.setPosition(it.toLong())
            Log.i("pos", it.toString())
        },
        valueRange = 0f..duration,
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



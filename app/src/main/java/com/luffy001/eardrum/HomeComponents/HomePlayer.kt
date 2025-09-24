package com.luffy001.eardrum.HomeComponents

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.luffy001.eardrum.PlayerComponents.msToTime
import com.luffy001.eardrum.R
import com.luffy001.eardrum.ViewModels.musicPlaylist
import com.luffy001.eardrum.ViewModels.uiModel
import com.luffy001.eardrum.lib.imageFromPath
import com.luffy001.eardrum.screens.Screens
import com.luffy001.eardrum.screens.navController
import com.luffy001.eardrum.service.PlaybackViewModel

@Composable
fun BoxPlayingMusic(viewModel: PlaybackViewModel) {
    val isPlaying by viewModel.isPlaying.observeAsState(false)
    val processAudio by viewModel.processAudio.observeAsState(0f)
    val audioPlaying by viewModel.audioPlaying.observeAsState(null)
    var process by remember { mutableFloatStateOf(0f) }
    val totalHeight = LocalConfiguration.current.screenHeightDp.dp * 0.18f
    val totalWidth = LocalConfiguration.current.screenWidthDp
    LaunchedEffect(isPlaying) {
        if (isPlaying) viewModel.runAudio()
    }
    LaunchedEffect(processAudio) {
        //porcentaje de pantalla : process * totalWidth
        process = processAudio * totalWidth.toFloat()
    }
    if (audioPlaying != null) {
        Row(Modifier.fillMaxWidth()) {
            Spacer(
                Modifier
                    .width(process.dp)
                    .height(2.dp)
                    .background(Color.Yellow)
            )
            Spacer(
                Modifier
                    .width(totalWidth.dp - process.dp)
                    .height(2.dp)
                    .background(Color.LightGray.copy(alpha = 0.5f))
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(totalHeight)
                .background(Color.Black)
                .padding(horizontal = 10.dp)
        ) {
            MusicSoundHome(viewModel) {
                navController.navigate(Screens.PlayerScreen.route + "/false")
            }
        }
    }
}

@Composable
fun MusicSoundHome(viewModel: PlaybackViewModel, onClick: () -> Unit) {
    val audio by viewModel.audioPlaying.observeAsState(null)
    val image = imageFromPath(audio?.contentUri)
    val painter = painterResource(R.drawable.ic_logosimple)
    val modifier = Modifier
        .width(70.dp)
        .height(50.dp)
        .clip(RoundedCornerShape(5.dp))
    Row(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clickable(onClick = { onClick() })
            .padding(horizontal = 2.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            Modifier
                .width(80.dp)
                .fillMaxHeight()
        ) {
            if (image !== null) {
                Image(
                    bitmap = image,
                    contentDescription = audio?.name ?: "",
                    modifier = modifier,
                )
            } else Image(painter, audio?.name ?: "", modifier = modifier)
        }
        Column(
            Modifier.width(max(140.dp, 200.dp))
        ) {
            Text(audio?.name ?: "", color = Color.White, maxLines = 1)
            Text(msToTime(audio?.duration?.toLong() ?: 0L), color = Color.White, maxLines = 1)
        }
        HandleMusicHome(viewModel)
    }
}

@Composable
fun HandleMusicHome(viewModel: PlaybackViewModel) {
    val isPlaying by viewModel.isPlaying.observeAsState(false)
    val tint = Color.White
    val playImage = painterResource(R.drawable.ic_play)
    val rightImage = painterResource(R.drawable.ic_next_arrow)
    val pauseImage = painterResource(R.drawable.ic_pause)
    IconButton(onClick = {
        viewModel.playAndStop()
    }) {
        Icon(
            painter = if (isPlaying) pauseImage else playImage,
            contentDescription = "PauseOrPlay",
            Modifier.size(40.dp),
            tint
        )
    }
    IconButton(onClick = {
        viewModel.previousNextAudio(true)
    }) {
        Icon(
            rightImage, "Right", modifier = Modifier.size(40.dp), tint
        )
    }
}
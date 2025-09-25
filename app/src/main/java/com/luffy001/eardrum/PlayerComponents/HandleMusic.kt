package com.luffy001.eardrum.PlayerComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.luffy001.eardrum.R
import com.luffy001.eardrum.screens.Screens
import com.luffy001.eardrum.screens.navController
import com.luffy001.eardrum.service.PlaybackViewModel

@Composable
fun HandleMusic(viewModel: PlaybackViewModel) {
    val isRandom by viewModel.isRandom.observeAsState(false)
    val isPlaying by viewModel.isPlaying.observeAsState(false)
    val tint = Color.White
    val leftImage = painterResource(R.drawable.ic_left_arrow)
    val playImage = painterResource(R.drawable.ic_play)
    val rightImage = painterResource(R.drawable.ic_right_arrow)
    val pauseImage = painterResource(R.drawable.ic_pause)
    val randomImage = painterResource(R.drawable.ic_random)
    val orderImage = painterResource(R.drawable.ic_order_playlist)
    val playlistImage = painterResource(R.drawable.ic_playlist)
    Icon(
        if (isRandom) orderImage else randomImage, "random", Modifier
            .clickable {
                changePLayer(viewModel)
            }
            .padding(start = 10.dp)
            .size(30.dp), tint
    )
    Icon(
        leftImage, "Left", Modifier
            .clickable {
                viewModel.previousNextAudio(false)
            }
            .size(80.dp), tint
    )
    Icon(
        painter = if (isPlaying) pauseImage else playImage,
        contentDescription = "PauseOrPlay",
        Modifier
            .size(80.dp)
            .clickable(onClick = {
                viewModel.playAndStop()
            }),
        tint
    )
    Icon(
        rightImage,
        "Right",
        Modifier
            .size(80.dp)
            .clickable(onClick = { viewModel.previousNextAudio(true) }),
        tint
    )
    Icon(
        playlistImage,
        "playlist",
        Modifier
            .clickable {
                navController.navigate(Screens.PlaylistReproductionScreen.route)
            }
            .size(40.dp),
        Color.Yellow
    )
}

fun changePLayer(viewModel: PlaybackViewModel) {
    viewModel.activeRandomMode()
}
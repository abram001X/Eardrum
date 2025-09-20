package com.luffy001.eardrum.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luffy001.eardrum.PlayerComponents.HandleMusic
import com.luffy001.eardrum.PlayerComponents.SliderM3
import com.luffy001.eardrum.PlayerComponents.VisPosition
import com.luffy001.eardrum.lib.imageFromPath
import com.luffy001.eardrum.R
import com.luffy001.eardrum.service.PlaybackViewModel
@Composable
fun InitPlayerApp(viewModel: PlaybackViewModel, isPrepared: Boolean = false) {
    LaunchedEffect(key1 = true) {
        if (isPrepared) {
            viewModel.prepareMedia()
        }
    }
    Scaffold(topBar = { TopBar2(Screens.HomeScreen.route, "Escuchando") }) { innerPadding ->
        val image = painterResource(id = R.drawable.background)
        Image(
            painter = image, contentDescription = "Background",
            modifier = Modifier
                .padding(top = 23.dp)
                .fillMaxSize()
        )
        PlayerApp(innerPadding, viewModel)
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar2(navigation: String, title: String) {
    val arrowBack = painterResource(R.drawable.ic_arrow_back)
    TopAppBar(title = {
        Icon(
            painter = arrowBack, contentDescription = "back", Modifier.clickable(onClick = {
                navController.navigate(navigation)
            })
        )

        Text(text = title, Modifier.padding(start = 30.dp))
    })
}

@Composable
fun ImagePlayer(viewModel: PlaybackViewModel) {
    val audioPlaying by viewModel.audioPlaying.observeAsState(null)
    val modifier = Modifier
        .height(400.dp)
        .width(350.dp)
        .padding(10.dp)
    val imageMusic = painterResource(R.drawable.ic_logosimple)
    val image = imageFromPath(audioPlaying?.contentUri)
    if (image !== null) {
        Image(bitmap = image, contentDescription = audioPlaying?.name, modifier)
    } else Image(
        painter = imageMusic, contentDescription = audioPlaying?.name, modifier = modifier

    )
}
@Composable
fun PlayerApp(padding: PaddingValues, viewModel: PlaybackViewModel) {
    val isPlaying by viewModel.isPlaying.observeAsState(false)
    val audioPlaying by viewModel.audioPlaying.observeAsState(null)
    LaunchedEffect(isPlaying) {
        if (isPlaying) viewModel.runAudio()
    }
    val tint = Color.White
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = padding.calculateTopPadding()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImagePlayer(viewModel)
        Spacer(Modifier.height(50.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .height(22.dp), Arrangement.Center, Alignment.CenterVertically
        ) {
            Text(text = audioPlaying?.name ?: "", color = tint, fontSize = 20.sp)
        }
        SliderM3(viewModel)
        VisPosition(viewModel)
        Row(
            Modifier
                .fillMaxWidth()
                .height(200.dp), Arrangement.Center, Alignment.CenterVertically
        ) {
            HandleMusic(viewModel)
        }
    }
}
package com.luffy001.eardrum.screens

import android.util.Log
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import com.luffy001.eardrum.lib.playerController

var audioFile by mutableStateOf(playerController.audioPlaying)

@Composable
fun InitPlayerApp() {
    LaunchedEffect(playerController.audioPlaying) {
        audioFile = playerController.audioPlaying
    }
    Scaffold(topBar = { TopBar2() }) { innerPadding ->
        val image = painterResource(id = R.drawable.background)
        Image(
            painter = image,
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize()
        )
        PlayerApp(innerPadding)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar2() {
    val arrowBack = painterResource(R.drawable.ic_arrow_back)
    TopAppBar(title = {
        Icon(
            painter = arrowBack,
            contentDescription = "back",
            Modifier
                .clickable(onClick = {
                    navController.navigate(Screens.HomeScreen.route)
                })
        )

        Text(text = "Eardrum", Modifier.padding(start = 30.dp))
    })
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
    } else Image(
        painter = imageMusic,
        contentDescription = audioFile.name,
        modifier = modifier

    )
}

@Composable
fun PlayerApp(padding: PaddingValues) {
    LaunchedEffect(playerController.isPlaying) {
        if (playerController.isPlaying) playerController.runAudio()
    }
    val tint = Color.White
    Column(
        Modifier
            .fillMaxSize()
            .padding(padding),
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

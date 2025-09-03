package com.luffy001.eardrum.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.luffy001.eardrum.R
import com.luffy001.eardrum.audioFiles
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.luffy001.eardrum.lib.AudioFile
import com.luffy001.eardrum.lib.PlayerController
import com.luffy001.eardrum.lib.audioList
import com.luffy001.eardrum.lib.imageFromPath

lateinit var playerController: PlayerController
lateinit var navController : NavController
@Composable
fun Component() {
    Box {
        ListMusic()
    }
}

@Composable
fun ListMusic() {
    LazyColumn(Modifier.fillMaxSize()) {
        items(audioFiles) { audio ->
            BoxData(audio)
        }
    }
}

@Composable
fun BoxData(audio: AudioFile) {
    val image = imageFromPath(audio.contentUri)
    val painter = painterResource(R.drawable.ic_logosimple)
    Row(
        Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable(onClick = { initPlayer(audio) })
    ) {
        Box(Modifier
            .width(80.dp)
            .fillMaxHeight()) {
            if (image !== null) {
                Image(
                    bitmap = image,
                    contentDescription = audio.name,
                    modifier = Modifier.fillMaxSize()
                )
            } else Image(painter, audio.name, Modifier.fillMaxSize())
        }
        Column(
            Modifier
                .fillMaxSize()
                .padding(start = 15.dp)
        ) {
            Text(audio.name, color = Color.White, maxLines = 1)
            Text(audio.duration.toString(), color = Color.White, maxLines = 1)
        }
    }
}

fun initPlayer(audio: AudioFile){
    playerController = PlayerController(audioList)
    playerController.prepareMedia()
    playerController.playAndStop()
    navController.navigate(Screens.PlayerScreen.route + "/${audio.id}")
}

@Composable
fun InitHome(navigation: NavController) {
    navController = navigation
    Component()
}

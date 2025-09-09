package com.luffy001.eardrum.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.luffy001.eardrum.R
import com.luffy001.eardrum.TopBar
import com.luffy001.eardrum.audioFiles
import com.luffy001.eardrum.lib.audioList
import com.luffy001.eardrum.lib.playerController

@Composable
fun PlayListReproduction() {
    LazyColumn(Modifier.fillMaxSize()) {
        items(audioFiles) { audio ->
            BoxData(audio) {
                val position = audioList.indexOf(audio)
                playerController.changeMusic(position)
                playerController.setIsPlaying()
                navController.navigate(Screens.PlayerScreen.route)
            }
        }
    }
}



@Composable
fun InitPlayListReproduction() {
    Scaffold(topBar = { TopBar2(Screens.PlayerScreen.route, "Siguiente a reproducir") }) { innerPadding ->
        val image = painterResource(id = R.drawable.background)
        Image(
            painter = image,
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize()
        )
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            PlayListReproduction()
        }
    }

}

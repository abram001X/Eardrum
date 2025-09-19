package com.luffy001.eardrum.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.luffy001.eardrum.HomeComponents.BoxData
import com.luffy001.eardrum.R
import com.luffy001.eardrum.lib.AudioFile
import com.luffy001.eardrum.service.PlaybackViewModel

@Composable
fun PlayListReproduction(viewModel: PlaybackViewModel) {
    val playlist by viewModel.playList.observeAsState(emptyList<AudioFile>())
    LazyColumn(Modifier.fillMaxSize()) {
        items(playlist) { audio ->
            BoxData(viewModel, audio) {
                val position = playlist.indexOf(audio)
                viewModel.changeMusic(position)
                navController.navigate(Screens.PlayerScreen.route + "/false")
            }
        }
    }
}


@Composable
fun InitPlayListReproduction(viewModel: PlaybackViewModel) {
    Scaffold(topBar = {
        TopBar2(
            Screens.PlayerScreen.route,
            "Siguiente a reproducir"
        )
    }) { innerPadding ->
        val image = painterResource(id = R.drawable.background)
        Image(
            painter = image, contentDescription = "Background",
            modifier = Modifier
                .padding(top = 23.dp)
                .fillMaxSize()
        )
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            PlayListReproduction(viewModel)
        }
    }

}

package com.luffy001.eardrum.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.luffy001.eardrum.HomeComponents.BoxData
import com.luffy001.eardrum.HomeComponents.BoxPlayingMusic
import com.luffy001.eardrum.HomeComponents.HeaderHome
import com.luffy001.eardrum.HomeComponents.OptionMusic
import com.luffy001.eardrum.R
import com.luffy001.eardrum.lib.AudioFile
import com.luffy001.eardrum.lib.musicPlaylist
import com.luffy001.eardrum.service.PlaybackViewModel

@Composable
fun InitPlaylist(viewModel: PlaybackViewModel, name: String = "") {
    Log.i("play", name)
    musicPlaylist.getMusicsPlaylist(name)
    Scaffold(topBar = { TopBar2(Screens.HomeScreen.route, name) }) { innerPadding ->
        val image = painterResource(id = R.drawable.background)
        Image(
            painter = image, contentDescription = "Background", modifier = Modifier.fillMaxSize()
        )
        Column(
            Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .fillMaxSize()
        ) {
            HeaderHome(viewModel, true)
            LazyColumn(Modifier.fillMaxSize()) {
                items(musicPlaylist.listMusicsModel) { audio ->
                    BoxData(viewModel, audio, true, name) {
                        val indexItem = musicPlaylist.listMusicsModel.indexOf(audio)
                        viewModel.setPlaylist(musicPlaylist.listMusicsModel, indexItem)
                        navController.navigate(Screens.PlayerScreen.route + "/true")
                    }
                }
            }
            BoxPlayingMusic(viewModel)
        }
    }
}


@Composable
fun MenuMusicPlaylist(isPlaylist: Boolean, audio: AudioFile, namePlaylist: String? = null) {
    var expanded by remember { mutableStateOf(false) }
    var expandedOptions by remember { mutableStateOf(false) }
    val optionIcon = painterResource(R.drawable.ic_option)
    Box(
    ) {

        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(
                painter = optionIcon, "options", modifier = Modifier
                    .size(30.dp), tint = Color.White
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Agregar a playlist") },
                onClick = { expandedOptions = true }
            )
            if (isPlaylist) {
                DropdownMenuItem(
                    text = { Text("Eliminar musica de playlist") },
                    onClick = {
                        musicPlaylist.removeMusicFromPlaylists(
                            namePlaylist ?: "",
                            audio.name
                        )
                    }
                )

            }
            if (expandedOptions) OptionMusic(audio)
        }
    }
}


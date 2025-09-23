package com.luffy001.eardrum.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.luffy001.eardrum.HomeComponents.BoxData
import com.luffy001.eardrum.HomeComponents.BoxPlayingMusic
import com.luffy001.eardrum.HomeComponents.HeaderHome
import com.luffy001.eardrum.HomeComponents.OptionMusic
import com.luffy001.eardrum.R
import com.luffy001.eardrum.lib.AudioFile
import com.luffy001.eardrum.ViewModels.musicPlaylist
import com.luffy001.eardrum.service.PlaybackViewModel

@Composable
fun InitPlaylist(viewModel: PlaybackViewModel, name: String = "") {
    val audioPlaying by viewModel.audioPlaying.observeAsState(null)
    val totalHeight = LocalConfiguration.current.screenHeightDp.dp
    val modifier =
        if (audioPlaying !== null) Modifier.height(totalHeight * 0.79f) else Modifier.fillMaxHeight()
    musicPlaylist.getMusicsPlaylist(name)
    Scaffold(topBar = { TopBarSearch(isPlaylist = true) }) { innerPadding ->
        val image = painterResource(id = R.drawable.background)
        Image(
            painter = image, contentDescription = "Background",
            modifier = Modifier
                .padding(top = 23.dp)
                .fillMaxSize()
        )
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            Column(
                modifier
            ) {
                HeaderHome(viewModel, true, isReproduction = false, name)
                LazyColumn(Modifier.fillMaxSize()) {
                    items(musicPlaylist.listMusicsModel) { audio ->
                        BoxData(viewModel, audio, true, name) {
                            val indexItem = musicPlaylist.listMusicsModel.indexOf(audio)
                            viewModel.setPlaylist(musicPlaylist.listMusicsModel, indexItem)
                            navController.navigate(Screens.PlayerScreen.route + "/true")
                        }
                    }
                }
            }
            BoxPlayingMusic(viewModel)
        }
    }
}


@Composable
fun MenuMusicPlaylist(
    viewModel: PlaybackViewModel,
    isPlaylist: Boolean,
    audio: AudioFile,
    namePlaylist: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var expandedOptions by remember { mutableStateOf(false) }
    val optionIcon = painterResource(R.drawable.ic_option)
    val playlist by viewModel.playList.observeAsState(emptyList<AudioFile>())

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
                text = { Text("Agregar a playlist", fontFamily = FontFamily.SansSerif) },
                onClick = { expandedOptions = true }
            )
            if (playlist.isNotEmpty()) DropdownMenuItem(
                text = { Text("Agregar a reproducción", fontFamily = FontFamily.SansSerif) },
                onClick = {
                    viewModel.addMediaToPlaylist(listOf(audio))
                    expanded = false
                }
            )
            if (isPlaylist) {
                DropdownMenuItem(
                    text = {
                        Text(
                            "Eliminar musica de playlist",
                            fontFamily = FontFamily.SansSerif
                        )
                    },
                    onClick = {
                        musicPlaylist.removeMusicFromPlaylists(
                            namePlaylist ?: "",
                            listOf(audio)
                        )
                    }
                )

            }
            if (expandedOptions) OptionMusic(listOf(audio))
        }
    }
}


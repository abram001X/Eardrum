package com.luffy001.eardrum.HomeComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.luffy001.eardrum.R
import com.luffy001.eardrum.lib.AudioFile
import com.luffy001.eardrum.ViewModels.interfaceViewModel
import com.luffy001.eardrum.ViewModels.musicPlaylist
import com.luffy001.eardrum.ViewModels.uiModel
import com.luffy001.eardrum.screens.Screens
import com.luffy001.eardrum.screens.navController
import com.luffy001.eardrum.service.PlaybackViewModel
import kotlin.random.Random

@Composable
fun HeaderHome(
    viewModel: PlaybackViewModel,
    isPlaylist: Boolean,
    isReproduction: Boolean? = false,
    namePlaylist: String = ""
) {
    val isRandom by viewModel.isRandom.observeAsState(false)
    val randomIcon = painterResource(R.drawable.ic_random)
    val noRandomIcon = painterResource(R.drawable.ic_order_playlist)
    if ((isReproduction == true && interfaceViewModel.isPress) || isReproduction == false) Row(
        Modifier
            .fillMaxWidth()
            .height(40.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (interfaceViewModel.isPress) {
            HandleMusicsSelected(viewModel, isPlaylist, namePlaylist)
        } else {
            Row {
                PlayHome(viewModel, isPlaylist)
                IconButton(onClick = { viewModel.activeRandomMode() }) {
                    Icon(
                        painter = if (isRandom) noRandomIcon else randomIcon,
                        tint = Color.White,
                        modifier = Modifier
                            .size(35.dp)
                            .padding(start = 10.dp),
                        contentDescription = "play"
                    )
                }
            }
            Box {
                OrderMusics(viewModel,isPlaylist)
            }
        }
    }
}

@Composable
fun HandleMusicsSelected(viewModel: PlaybackViewModel, isPlaylist: Boolean, namePlaylist: String) {
    val exitIcon = painterResource(R.drawable.ic_remove_x)
    val optionIcon = painterResource(R.drawable.ic_option)
    var expanded by remember { mutableStateOf(false) }
    var expandedOptions by remember { mutableStateOf(false) }
    val playlist by viewModel.playList.observeAsState(emptyList<AudioFile>())

    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { interfaceViewModel.activatePressed(false) }) {
            Icon(
                painter = exitIcon,
                tint = Color.White,
                modifier = Modifier
                    .size(35.dp)
                    .padding(start = 10.dp),
                contentDescription = "play"
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(
            "${interfaceViewModel.countElements} Seleccionado",
            color = Color.White,
            fontFamily = FontFamily.SansSerif
        )
    }

    IconButton(onClick = { expanded = true }) {
        Icon(
            painter = optionIcon,
            tint = Color.White,
            modifier = Modifier
                .size(33.dp)
                .padding(start = 10.dp),
            contentDescription = "options"
        )
        Box() {
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text("Agregar a playlist", fontFamily = FontFamily.SansSerif) },
                    onClick = { expandedOptions = true }
                )
                DropdownMenuItem(
                    text = { Text("Reproduciir", fontFamily = FontFamily.SansSerif) },
                    onClick = {
                        viewModel.setPlaylist(interfaceViewModel.elementsSelected, 0)
                        interfaceViewModel.activatePressed(false)
                        navController.navigate(Screens.PlayerScreen.route + "/true")
                    }
                )
                if (isPlaylist) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Eliminar de playlist",
                                fontFamily = FontFamily.SansSerif
                            )
                        },
                        onClick = {
                            musicPlaylist.removeMusicFromPlaylists(
                                namePlaylist,
                                interfaceViewModel.elementsSelected
                            )
                            expanded = false
                            interfaceViewModel.activatePressed(false)
                        }
                    )
                }
                if (playlist.isNotEmpty()) DropdownMenuItem(
                    text = { Text("Agregar a reproducción", fontFamily = FontFamily.SansSerif) },
                    onClick = {
                        viewModel.addMediaToPlaylist(interfaceViewModel.elementsSelected)
                        interfaceViewModel.activatePressed(false)
                        expanded = false
                    }
                )
                if (expandedOptions) OptionMusic(interfaceViewModel.elementsSelected)
            }
        }
    }
}

@Composable
fun OrderMusics(viewModel: PlaybackViewModel,isPlaylist: Boolean) {
    var expanded by remember { mutableStateOf(false) }
    val orderMusic = painterResource(R.drawable.ic_order)
    IconButton(onClick = { expanded = true }) {
        Icon(
            painter = orderMusic,
            tint = Color.White,
            modifier = Modifier.size(30.dp),
            contentDescription = "play"
        )
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        DropdownMenuItem(
            text = { Text("Ordenar alfabeticamente") },
            onClick = {
                if (isPlaylist) {
                    musicPlaylist.setPlaylistModel(musicPlaylist.listMusicsModel.sortedBy { it.name })
                } else {
                    uiModel.setAudioList(uiModel.musicsList.sortedBy { it.name })
                }
            }
        )
        DropdownMenuItem(
            text = { Text("Ordernar por fecha") },
            onClick = {
                if (isPlaylist) {
                    musicPlaylist.setPlaylistModel(musicPlaylist.listMusicsModel.sortedBy { it.date }
                        .reversed())
                } else {
                    uiModel.setAudioList(uiModel.musicsList.sortedBy { it.date }.reversed())
                }
            }
        )
    }
}

@Composable
fun PlayHome(viewModel: PlaybackViewModel, isPlaylist: Boolean) {
    val isRandom by viewModel.isRandom.observeAsState(false)
    val playIcon = painterResource(R.drawable.ic_play)
    IconButton(onClick = {
        if (isPlaylist) { // usar listas distintas
            val randomPosition = Random.nextInt(musicPlaylist.listMusicsModel.size)
            val position = if (isRandom) randomPosition else 0
            viewModel.setPlaylist(musicPlaylist.listMusicsModel, position)
        } else {
            val randomPosition = Random.nextInt(uiModel.musicsList.size)
            val position = if (isRandom) randomPosition else 0
            viewModel.setPlaylist(uiModel.musicsList, position)
        }
        navController.navigate(Screens.PlayerScreen.route + "/true")
    }) {
        Icon(
            painter = playIcon,
            tint = Color.White,
            modifier = Modifier.size(30.dp),
            contentDescription = "play"
        )
    }
}
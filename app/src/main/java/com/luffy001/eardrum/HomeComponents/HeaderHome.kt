package com.luffy001.eardrum.HomeComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.luffy001.eardrum.R
import com.luffy001.eardrum.audioFiles
import com.luffy001.eardrum.lib.musicPlaylist
import com.luffy001.eardrum.lib.uiModel
import com.luffy001.eardrum.screens.Screens
import com.luffy001.eardrum.screens.navController
import com.luffy001.eardrum.service.PlaybackViewModel
import kotlin.random.Random

@Composable
fun HeaderHome(viewModel: PlaybackViewModel, isPlaylist: Boolean) {
    var isRandom by remember { mutableStateOf(false) }
    val isRandomModel by viewModel.isRandom.observeAsState(false)
    LaunchedEffect(isRandomModel) {
        isRandom = isRandomModel
    }
    val randomIcon = painterResource(R.drawable.ic_random)
    val noRandomIcon = painterResource(R.drawable.ic_order_playlist)
    Row(
        Modifier
            .fillMaxWidth()
            .height(40.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            PlayHome(viewModel, isRandom, isPlaylist)
            IconButton(onClick = { isRandom = !isRandom }) {
                Icon(
                    painter = if (isRandom) noRandomIcon else randomIcon,
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                        .padding(start = 10.dp),
                    contentDescription = "play"
                )
            }
        }
        Box {
            OrderMusics(isPlaylist)
        }
    }
}

@Composable
fun OrderMusics(isPlaylist: Boolean) {
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

                    uiModel.setAudioList(audioFiles.sortedBy { it.name })
                }
            }
        )
        DropdownMenuItem(
            text = { Text("Ordernar por fecha") },
            onClick = {
                if (isPlaylist) {
                    musicPlaylist.setPlaylistModel(musicPlaylist.listMusicsModel.sortedBy { it.date })
                } else {

                    uiModel.setAudioList(audioFiles.sortedBy { it.date })
                }
            }
        )

    }

}

@Composable
fun PlayHome(viewModel: PlaybackViewModel, isRandom: Boolean, isPlaylist: Boolean) {
    val playIcon = painterResource(R.drawable.ic_play)
    IconButton(onClick = {
        if (isPlaylist) { // usar listas distintas
            val randomPosition = Random.nextInt(musicPlaylist.listMusicsModel.size)
            val position = if (isRandom) {
                randomPosition
            } else {
                0
            }
            viewModel.setPlaylist(musicPlaylist.listMusicsModel, position)
        } else {
            val randomPosition = Random.nextInt(uiModel.musicsList.size)
            val position = if (isRandom) randomPosition else 0
            viewModel.setPlaylist(uiModel.musicsList, position)
        }
        if (isRandom) {
            viewModel.activeRandomMode()
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
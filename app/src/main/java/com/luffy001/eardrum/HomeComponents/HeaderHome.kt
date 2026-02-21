package com.luffy001.eardrum.HomeComponents

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.collectAsState
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
import com.luffy001.eardrum.Animations.RandomAnimation
import com.luffy001.eardrum.R
import com.luffy001.eardrum.lib.AudioFile
import com.luffy001.eardrum.ViewModels.interfaceViewModel
import com.luffy001.eardrum.ViewModels.musicPlaylist
import com.luffy001.eardrum.ViewModels.uiModel
import com.luffy001.eardrum.lib.deleteFilesAudio
import com.luffy001.eardrum.lib.deleteOneAudio
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
    val isRandom by viewModel.isRandom.collectAsState()
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
                    RandomAnimation(isRandom)
                }
            }
            Box {
                OrderMusics(viewModel, isPlaylist)
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
    fun closeMusicSelected() {
        expanded = false
        expandedOptions = false
    }
    val deleteLauncherIntent = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ){result->
        if(result.resultCode == Activity.RESULT_OK){
            Log.i("deleteFile", "archivo eliminado")
        }
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {
            interfaceViewModel.activatePressed(false)
            closeMusicSelected()
        }) {
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
            DropdownMenu(expanded = expanded, onDismissRequest = { closeMusicSelected() }) {
                DropdownMenuItem(
                    text = { Text("Agregar a playlist", fontFamily = FontFamily.SansSerif) },
                    onClick = { expandedOptions = true
                    closeMusicSelected()}
                )
                DropdownMenuItem(
                    text = { Text("Reproduciir", fontFamily = FontFamily.SansSerif) },
                    onClick = {
                        viewModel.setPlaylist(interfaceViewModel.elementsSelected, 0)
                        interfaceViewModel.activatePressed(false)
                        closeMusicSelected()
                        navController.navigate(Screens.PlayerScreen.route)
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
                            closeMusicSelected()
                            interfaceViewModel.activatePressed(false)
                        }
                    )
                }
                if (playlist.isNotEmpty()) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Agregar a reproducción",
                                fontFamily = FontFamily.SansSerif
                            )
                        },
                        onClick = {
                            viewModel.addMediaToPlaylist(interfaceViewModel.elementsSelected)
                            interfaceViewModel.activatePressed(false)
                            closeMusicSelected()
                        }
                    )
                }
                if (expandedOptions) {
                    MenuListsPlaylists(interfaceViewModel.elementsSelected) {closeMusicSelected()}
                }
                if (!isPlaylist) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Eliminar archivos",
                                fontFamily = FontFamily.SansSerif,
                                color = Color.Red
                            )
                        },
                        onClick = {
                            deleteFilesAudio(interfaceViewModel.elementsSelected.map { it -> it.contentUri },deleteLauncherIntent)
                            closeMusicSelected()
                            interfaceViewModel.activatePressed(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OrderMusics(viewModel: PlaybackViewModel, isPlaylist: Boolean) {
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
            text = { Text("alfabéticamente A-Z") },
            onClick = {
                if (isPlaylist) {
                    musicPlaylist.setPlaylistModel(musicPlaylist.listMusicsModel.sortedBy { it.name })
                } else {
                    uiModel.onOrderList("abc")
                }
            }
        )
        DropdownMenuItem(
            text = { Text("alfabéticamente Z-A") },
            onClick = {
                if (isPlaylist) {
                    musicPlaylist.setPlaylistModel(musicPlaylist.listMusicsModel.sortedBy { it.name }
                        .reversed())
                } else {
                    uiModel.onOrderList("cba")
                }
            }
        )
        DropdownMenuItem(
            text = { Text("Más recientes") },
            onClick = {
                if (isPlaylist) {
                    musicPlaylist.setPlaylistModel(musicPlaylist.listMusicsModel.sortedBy { it.date }
                        .reversed())
                } else {
                    uiModel.onOrderList("date")
                }
            }
        )
        DropdownMenuItem(
            text = { Text("Más antiguos") },
            onClick = {
                if (isPlaylist) {
                    musicPlaylist.setPlaylistModel(musicPlaylist.listMusicsModel.sortedBy { it.date })
                } else {
                    uiModel.onOrderList("datereversed")
                }
            }
        )
    }
}

@Composable
fun PlayHome(viewModel: PlaybackViewModel, isPlaylist: Boolean) {
    val items by uiModel.items.collectAsState()
    val isRandom by viewModel.isRandom.collectAsState()
    val playIcon = painterResource(R.drawable.ic_play)
    IconButton(onClick = {
        if (isPlaylist) { // usar listas distintas
            val randomPosition = Random.nextInt(musicPlaylist.listMusicsModel.size)
            val position = if (isRandom) randomPosition else 0
            viewModel.setPlaylist(musicPlaylist.listMusicsModel, position)
        } else {
            val randomPosition = Random.nextInt(items.size)
            val position = if (isRandom) randomPosition else 0
            viewModel.setPlaylist(items, position)
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
package com.luffy001.eardrum.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luffy001.eardrum.HomeComponents.OptionMusic
import com.luffy001.eardrum.PlayerComponents.HandleMusic
import com.luffy001.eardrum.PlayerComponents.SliderM3
import com.luffy001.eardrum.PlayerComponents.VisPosition
import com.luffy001.eardrum.lib.imageFromPath
import com.luffy001.eardrum.R
import com.luffy001.eardrum.ViewModels.musicPlaylist
import com.luffy001.eardrum.lib.AudioFile
import com.luffy001.eardrum.ViewModels.uiModel
import com.luffy001.eardrum.service.PlaybackViewModel

@Composable
fun InitPlayerApp(viewModel: PlaybackViewModel, isPrepared: Boolean = false) {
    LaunchedEffect(key1 = true) {
        musicPlaylist.searchMusicByName("")
        uiModel.searchMusicByName("")
        if (isPrepared) {
            viewModel.prepareMedia()
        }
    }
    Scaffold(topBar = { TopBar2(Screens.HomeScreen.route, "Escuchando") }) { innerPadding ->
        PlayerApp(innerPadding, viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar2(navigation: String, title: String) {
    val arrowBack = painterResource(R.drawable.ic_arrow_back)
    TopAppBar(
        title = {
            Row(
                Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    navController.navigate(navigation)
                }) {
                    Icon(
                        painter = arrowBack, contentDescription = "back", tint = Color.White
                    )
                }
                Text(text = title, Modifier.padding(start = 10.dp), color = Color.White)
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black,
        )
    )
}

@Composable
fun ImagePlayer(viewModel: PlaybackViewModel) {
    val optionIcon = painterResource(R.drawable.ic_option)
    var expanded by remember { mutableStateOf(false) }
    var expandedOptions by remember { mutableStateOf(false) }
    val audioPlaying by viewModel.audioPlaying.observeAsState(null)
    val totalHeight = LocalConfiguration.current.screenHeightDp
    val modifier = Modifier
        .height(300.dp)
        .width(350.dp)
        .padding(10.dp)
    val imageMusic = painterResource(R.drawable.ic_logosimple)
    val image = imageFromPath(audioPlaying?.contentUri)
    Column(
        Modifier
            .fillMaxWidth()
            .height(totalHeight.dp * 0.55f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (image !== null) {
            Image(bitmap = image, contentDescription = audioPlaying?.name, modifier)
        } else Image(
            painter = imageMusic, contentDescription = audioPlaying?.name, modifier = modifier
        )
        Row(
            Modifier
                .fillMaxWidth()
                .height(40.dp), horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    painter = optionIcon,
                    tint = Color.White,
                    modifier = Modifier
                        .size(37.dp),
                    contentDescription = "options"
                )
                Box() {
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = {
                            Text(
                                "Agregar a playlist", fontFamily = FontFamily.SansSerif
                            )
                        }, onClick = { expandedOptions = true })
                        if (expandedOptions) {
                            OptionMusic(listOf<AudioFile>(audioPlaying ?: uiModel.musicsList[0]))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerApp(padding: PaddingValues, viewModel: PlaybackViewModel) {
    val isPlaying by viewModel.isPlaying.observeAsState(false)
    val audioPlaying by viewModel.audioPlaying.observeAsState(null)
    val totalHeight = LocalConfiguration.current.screenHeightDp
    LaunchedEffect(isPlaying) {
        if (isPlaying) viewModel.runAudio()
    }
    val tint = Color.White

    Column(
        Modifier
            .fillMaxSize()
            .padding(top = padding.calculateTopPadding())
            .background(color = Color.Black), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImagePlayer(viewModel)
        Column(
            Modifier
                .fillMaxWidth()
                .height(totalHeight.dp * 0.45f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(22.dp),
                Arrangement.Center,
                Alignment.CenterVertically
            ) {
                Text(text = audioPlaying?.name ?: "", color = tint, fontSize = 20.sp)
            }
            SliderM3(viewModel)
            VisPosition(viewModel)
            Spacer(Modifier.height(20.dp))
            Row(
                Modifier
                    .fillMaxWidth(),
                Arrangement.SpaceAround,
                Alignment.CenterVertically
            ) {
                HandleMusic(viewModel)
            }
        }
    }
}
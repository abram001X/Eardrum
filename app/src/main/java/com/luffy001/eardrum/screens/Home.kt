package com.luffy001.eardrum.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.luffy001.eardrum.R
import com.luffy001.eardrum.audioFiles
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.luffy001.eardrum.HomeComponents.BoxData
import com.luffy001.eardrum.HomeComponents.BoxPlayingMusic
import com.luffy001.eardrum.HomeComponents.HeaderHome
import com.luffy001.eardrum.Pages.InitDownloadPage
import com.luffy001.eardrum.Pages.InitPlayListsPage
import com.luffy001.eardrum.Pages.SessionsPages
import com.luffy001.eardrum.TopBar
import com.luffy001.eardrum.lib.interfaceViewModel
import com.luffy001.eardrum.lib.uiModel
import com.luffy001.eardrum.service.PlaybackViewModel

lateinit var navController: NavController

@Composable
fun Component(viewModel: PlaybackViewModel) {

    val audioPlaying by viewModel.audioPlaying.observeAsState(null)
    val pagerState = rememberPagerState(pageCount = {
        3
    })
    val components: List<@Composable () -> Unit> = listOf({
        ListMusic(viewModel)
    }, { InitPlayListsPage() }, { InitDownloadPage() })
    val totalHeight = LocalConfiguration.current.screenHeightDp.dp
    val modifier =
        if (audioPlaying != null) Modifier.height(totalHeight * 0.79f) else Modifier.fillMaxHeight()
    Column(modifier) {
        SessionsPages(pagerState)
        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 3,
            userScrollEnabled = !interfaceViewModel.isPress
        ) { page ->
            components[page]()
        }
    }
    BoxPlayingMusic(viewModel)
}

@Composable
fun ListMusic(viewModel: PlaybackViewModel) {
    LaunchedEffect(audioFiles) {
        uiModel.setAudioList(audioFiles)
    }
    Column(Modifier.fillMaxSize()) {
        HeaderHome(viewModel, false)
        LazyColumn(Modifier.fillMaxSize()) {
            items(uiModel.musicsList) { audio ->
                BoxData(viewModel, audio) {
                    val indexItem = uiModel.musicsList.indexOf(audio)
                    viewModel.setPlaylist(uiModel.musicsList, indexItem)
                    navController.navigate(Screens.PlayerScreen.route + "/true")
                }
            }
        }
    }
}

@Composable
fun InitHome(navigation: NavController, viewModel: PlaybackViewModel) {
    navController = navigation
    Scaffold(topBar = { TopBar() }) { innerPadding ->
        val image = painterResource(id = R.drawable.background)
        Image(
            painter = image, contentDescription = "Background",
            modifier = Modifier
                .padding(top = 23.dp)
                .fillMaxSize()
        )
        Column(
            Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .fillMaxSize()
        ) {
            Component(viewModel)
        }
    }
}

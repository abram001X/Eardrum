package com.luffy001.eardrum.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
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
import com.luffy001.eardrum.lib.PlayerViewModel
import com.luffy001.eardrum.lib.playerController
import com.luffy001.eardrum.lib.uiModel
lateinit var navController: NavController

@Composable
fun Component() {
    val pagerState = rememberPagerState(pageCount = {
        3
    })
    val components: List<@Composable () -> Unit> = listOf({
        ListMusic()
    }, { InitPlayListsPage() }, { InitDownloadPage() })

    val totalHeight = LocalConfiguration.current.screenHeightDp.dp
    Column(modifier = Modifier.height(totalHeight * 0.79f)) {
        SessionsPages(pagerState)
        HeaderHome(false)
        HorizontalPager(state = pagerState, beyondViewportPageCount = 3) { page ->
            components[page]()
        }
    }
    BoxPlayingMusic()
}

@Composable
fun ListMusic() {
    LaunchedEffect(audioFiles) {
        uiModel.setAudioList(audioFiles)
    }
    LazyColumn(Modifier.fillMaxSize()) {
        items(uiModel.musicsList) { audio ->
            BoxData(audio) {
                val indexItem = uiModel.musicsList.indexOf(audio)
                playerController = PlayerViewModel(uiModel.musicsList, indexItem)
                playerController.prepareMedia()
                playerController.setIsPlaying()
                navController.navigate(Screens.PlayerScreen.route)
            }
        }
    }
}

@Composable
fun InitHome(navigation: NavController) {
    navController = navigation
    Scaffold(topBar = { TopBar() }) { innerPadding ->
        val image = painterResource(id = R.drawable.background)
        Image(
            painter = image, contentDescription = "Background", modifier = Modifier.fillMaxSize()
        )
        Column(
            Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .fillMaxSize()
        ) {
            Component()
        }
    }
}

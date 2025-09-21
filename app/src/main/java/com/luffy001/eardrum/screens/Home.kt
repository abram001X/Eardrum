package com.luffy001.eardrum.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.luffy001.eardrum.R
import com.luffy001.eardrum.audioFiles
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.luffy001.eardrum.HomeComponents.BoxData
import com.luffy001.eardrum.HomeComponents.BoxPlayingMusic
import com.luffy001.eardrum.HomeComponents.HeaderHome
import com.luffy001.eardrum.Pages.InitDownloadPage
import com.luffy001.eardrum.Pages.InitPlayListsPage
import com.luffy001.eardrum.Pages.SessionsPages
import com.luffy001.eardrum.lib.AudioFile
import com.luffy001.eardrum.lib.HandleMusicPlaylist
import com.luffy001.eardrum.lib.interfaceViewModel
import com.luffy001.eardrum.lib.musicPlaylist
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
    Scaffold(topBar = { TopBarSearch(isPlaylist = false) }) { innerPadding ->
        val image = painterResource(id = R.drawable.background)
        Image(
            painter = image,
            contentDescription = "Background",
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarSearch(isPlaylist: Boolean) {
    val searchIcon = painterResource(R.drawable.ic_search_icon)
    var expanded by remember { mutableStateOf(false) }
    var nameMusic by remember { mutableStateOf("") }
    val arrowBack = painterResource(R.drawable.ic_arrow_back)
    TopAppBar(title = {
        Row(
            horizontalArrangement = if (!expanded) Arrangement.SpaceBetween else Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!expanded) {
                Text(text = "Eardrum")
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        painter = searchIcon,
                        tint = Color.Black,
                        modifier = Modifier.size(30.dp),
                        contentDescription = "play"
                    )
                }
            } else {
                IconButton(onClick = {
                    if (!isPlaylist) {

                        uiModel.searchMusicByName("")
                    } else musicPlaylist.searchMusicByName(
                        ""
                    )
                    expanded = false
                }) {
                    Icon(
                        painter = arrowBack,
                        tint = Color.Black,
                        modifier = Modifier.size(30.dp),
                        contentDescription = "back"
                    )
                }
                TextField(
                    value = nameMusic,
                    onValueChange = { it ->
                        nameMusic = it
                        if (!isPlaylist) uiModel.searchMusicByName(it) else musicPlaylist.searchMusicByName(
                            it
                        )
                    },
                    placeholder = { Text("Buscar música") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    textStyle = TextStyle(fontSize = 20.sp),
                    maxLines = 1
                )
            }
        }
    })
}
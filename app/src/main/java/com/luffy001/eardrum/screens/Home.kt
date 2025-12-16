package com.luffy001.eardrum.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.luffy001.eardrum.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.luffy001.eardrum.HomeComponents.BoxData
import com.luffy001.eardrum.HomeComponents.BoxPlayingMusic
import com.luffy001.eardrum.HomeComponents.HeaderHome
import com.luffy001.eardrum.MyApplication
import com.luffy001.eardrum.Pages.InitDownloadPage
import com.luffy001.eardrum.Pages.InitPlayListsPage
import com.luffy001.eardrum.Pages.SessionsPages
import com.luffy001.eardrum.ViewModels.interfaceViewModel
import com.luffy001.eardrum.ViewModels.musicPlaylist
import com.luffy001.eardrum.ViewModels.uiModel
import com.luffy001.eardrum.lib.loadFilesAudio
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
    val items by uiModel.items.collectAsState()
    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(7.dp))
        HeaderHome(viewModel, false)
        LazyColumn(Modifier.fillMaxSize()) {
            items(items) { audio ->
                BoxData(viewModel, audio) {
                    val indexItem = items.indexOf(audio)
                    viewModel.setPlaylist(items, indexItem)
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
        Column(
            Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .fillMaxSize()
                .background(color = Color.Black)
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
    val arrowBack = painterResource(R.drawable.ic_remove_x)
    val totalWidth = LocalConfiguration.current.screenWidthDp.dp
    Box(
        modifier = Modifier.background(
            Color.Black
        )
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent, // Color de fondo personalizado (un azul vibrante)
            ), title = {
                Row(
                    horizontalArrangement = if (!expanded) Arrangement.SpaceBetween else Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(0.dp)
                        .fillMaxWidth()
                ) {
                    if (!expanded) {
                        Text(text = "Eardrum", color = Color.White)
                        Button(onClick = {
                            uiModel.setAudioList(loadFilesAudio(MyApplication.instance.contentResolver))
                            Log.i("reload", "lista de musica recargada")
                        }) { Text(text = "recargar")}
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                painter = searchIcon,
                                tint = Color.White,
                                modifier = Modifier.size(25.dp),
                                contentDescription = "play"
                            )
                        }
                    } else {
                        BasicTextField(
                            value = nameMusic,
                            onValueChange = { it ->
                                nameMusic = it
                                if (!isPlaylist) uiModel.searchMusicByName(it) else musicPlaylist.searchMusicByName(
                                    it
                                )
                            },
                            modifier = Modifier
                                .width(totalWidth * 0.80f)
                                .clip(RoundedCornerShape(7.dp))
                                .height(40.dp)
                                .background(Color.LightGray.copy(alpha = 0.4f))
                                .padding(7.dp),
                            textStyle = TextStyle(fontSize = 20.sp, color = Color.White),
                            maxLines = 1
                        )

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
                                tint = Color.White,
                                modifier = Modifier.size(30.dp),
                                contentDescription = "back"
                            )
                        }
                    }
                }
            })
    }
}
package com.luffy001.eardrum.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.luffy001.eardrum.HomeComponents.BoxPlayingMusic
import com.luffy001.eardrum.Pages.InitDownloadPage
import com.luffy001.eardrum.Pages.InitPlayListsPage
import com.luffy001.eardrum.Pages.PlaylistSelect
import com.luffy001.eardrum.Pages.SessionsPages
import com.luffy001.eardrum.PlayerComponents.msToTime
import com.luffy001.eardrum.TopBar
import com.luffy001.eardrum.lib.AudioFile
import com.luffy001.eardrum.lib.PlayerViewModel
import com.luffy001.eardrum.lib.audioList
import com.luffy001.eardrum.lib.imageFromPath
import com.luffy001.eardrum.lib.playerController

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
        HorizontalPager(state = pagerState, beyondViewportPageCount = 3) { page ->
            components[page]()
        }
    }
    BoxPlayingMusic()
}

@Composable
fun ListMusic() {
    LazyColumn(Modifier.fillMaxSize()) {
        items(audioFiles) { audio ->
            BoxData(audio) {
                val indexItem = audioList.indexOf(audio)
                playerController = PlayerViewModel(audioList, indexItem)
                playerController.prepareMedia()
                playerController.setIsPlaying()
                navController.navigate(Screens.PlayerScreen.route)
            }
        }
    }
}

@Composable
fun BoxData(
    audio: AudioFile,
    isPlaylist: Boolean = false,
    namePlaylist: String? = null,
    onClick: () -> Unit
) {
    val totalWidth = LocalConfiguration.current.screenWidthDp.dp
    val modifier = if (audio == playerController.audioPlaying) {
        Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable(onClick = { onClick() })
            .background(Color.Black.copy(alpha = 0.5f))
            .fillMaxWidth()
    } else {
        Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable(onClick = { onClick() })
            .fillMaxWidth()
    }
    val image = imageFromPath(audio.contentUri)
    val painter = painterResource(R.drawable.ic_logosimple)
    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 5.dp)
        ) {
            Box(
                Modifier
                    .width(80.dp)
                    .fillMaxHeight()
            ) {
                if (image !== null) {
                    Image(
                        bitmap = image,
                        contentDescription = audio.name,
                        modifier = Modifier.fillMaxSize()
                    )
                } else Image(
                    painter, audio.name, Modifier
                        .width(70.dp)
                        .height(60.dp)
                )
            }
            Column(
                Modifier
                    .fillMaxHeight()
                    .width(totalWidth * 0.7f)
                    .padding(start = 15.dp)
            ) {
                Text(audio.name, color = Color.White, maxLines = 1)
                Text(msToTime(audio.duration.toLong()), color = Color.White, maxLines = 1)
            }
            MenuMusicPlaylist(isPlaylist, audio, namePlaylist)
        }
    }
    Spacer(Modifier.height(10.dp))
}

@Composable
fun OptionMusic(audio: AudioFile) {
    var isSelectOptions by remember { mutableStateOf(true) }
    if (isSelectOptions) {
        Box() {
            Popup(
                alignment = Alignment.Center,
                properties = PopupProperties(),
                onDismissRequest = { isSelectOptions = false }
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .height(230.dp)
                        .background(Color.Black)
                ) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Agregar musica a playlist:",
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(Modifier.height(13.dp))
                    PlaylistSelect(audio.contentUri, audio.name)
                }
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

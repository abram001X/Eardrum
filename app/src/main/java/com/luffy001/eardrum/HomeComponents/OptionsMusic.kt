package com.luffy001.eardrum.HomeComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.luffy001.eardrum.Pages.PlaylistSelect
import com.luffy001.eardrum.PlayerComponents.msToTime
import com.luffy001.eardrum.R
import com.luffy001.eardrum.lib.AudioFile
import com.luffy001.eardrum.lib.imageFromPath
import com.luffy001.eardrum.lib.interfaceViewModel
import com.luffy001.eardrum.screens.MenuMusicPlaylist
import com.luffy001.eardrum.service.PlaybackViewModel

@Composable
fun ChildBoxData(
    audio: AudioFile, isPlaylist: Boolean, namePlaylist: String? = null
) {
    Row(
        modifier = Modifier.padding(horizontal = 5.dp)
    ) {
        ContentBoxData(audio, isPlaylist, namePlaylist)
    }
}

@Composable
fun BoxData(
    viewModel: PlaybackViewModel,
    audio: AudioFile,
    isPlaylist: Boolean = false,
    namePlaylist: String? = null,
    onClick: () -> Unit
) {
    var colorBackground by remember { mutableStateOf(Color.Transparent) }
    var isSelected by remember { mutableStateOf(false) }
    var selectOrNavigate by remember { mutableStateOf({}) }
    var selectMusic by remember { mutableStateOf({}) }
    val audioPlaying by viewModel.audioPlaying.observeAsState(null)
    LaunchedEffect(interfaceViewModel.isPress) {
        if (!interfaceViewModel.isPress) isSelected = false
    }
    LaunchedEffect(isSelected) {
        colorBackground =
            if (isSelected) {
                Color.LightGray.copy(alpha = 0.5f)
            } else if (audio.contentUri == (audioPlaying?.contentUri ?: "")) {
                Color.Black.copy(alpha = 0.5f)
            } else Color.Transparent
        selectOrNavigate = {
            if (isSelected) {
                interfaceViewModel.removeMusicSelect(audio)
                isSelected = false
            } else if (interfaceViewModel.isPress) {
                interfaceViewModel.setElementsSelected(
                    audio
                )
                isSelected = true
            } else onClick()
        }
        selectMusic = {
            if (isSelected) {
                interfaceViewModel.removeMusicSelect(audio)
                isSelected = false
            } else {

                interfaceViewModel.setElementsSelected(audio)
                interfaceViewModel.activatePressed(true)
                isSelected = true
            }
        }
    }
    val modifier = Modifier
        .fillMaxWidth()
        .height(70.dp)
        .combinedClickable(onClick = {
            selectOrNavigate()
        }, onLongClick = { selectMusic() })
        .background(colorBackground)

    Spacer(Modifier.height(10.dp))
    Box(
        modifier = modifier
    ) {
        ChildBoxData(audio, isPlaylist, namePlaylist)
    }
}

@Composable
fun ContentBoxData(audio: AudioFile, isPlaylist: Boolean, namePlaylist: String? = null) {
    val image = imageFromPath(audio.contentUri)
    val totalWidth = LocalConfiguration.current.screenWidthDp.dp
    val painter = painterResource(R.drawable.ic_logosimple)
    Box(
        Modifier
            .width(70.dp)
            .height(70.dp)
    ) {
        if (image !== null) {
            Image(
                bitmap = image,
                contentDescription = audio.name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(5.dp)),
                contentScale = ContentScale.Crop,
            )
        } else Image(
            painter,
            audio.name,
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(5.dp)),
            contentScale = ContentScale.Crop
        )
    }
    Column(
        Modifier
            .fillMaxHeight()
            .width(totalWidth * 0.7f)
            .padding(start = 15.dp)
    ) {
        Text(
            audio.name,
            color = Color.White,
            maxLines = 2,
            fontFamily = FontFamily.SansSerif,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            msToTime(audio.duration.toLong()),
            color = Color.White,
            fontFamily = FontFamily.SansSerif,
            maxLines = 1,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
    if (!interfaceViewModel.isPress) MenuMusicPlaylist(isPlaylist, audio, namePlaylist)
}

@Composable
fun OptionMusic(listAudio: List<AudioFile>) {
    var isSelectOptions by remember { mutableStateOf(true) }
    if (isSelectOptions) {
        Box(
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(5.dp)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Dialog(
                onDismissRequest = {
                    isSelectOptions = false
                    interfaceViewModel.activatePressed(false)
                }) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .height(230.dp)
                        .background(Color.Black)
                        .clip(RoundedCornerShape(5.dp))
                ) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
                        Text(
                            text = "Agregar a playlist:",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                    Spacer(Modifier.height(13.dp))
                    PlaylistSelect(listAudio)
                }
            }
        }
    }
}

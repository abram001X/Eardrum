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

fun handleModifierBoxData(
    audio: AudioFile,
    audioPlaying: AudioFile? = null,
    onClick: () -> Unit
): Modifier {
    val modifier = if (audio.contentUri == (audioPlaying?.contentUri ?: "")) {
        Modifier
            .fillMaxWidth()
            .height(70.dp)
            .combinedClickable(
                onClick = {
                    if (interfaceViewModel.isPress) interfaceViewModel.setElementsSelected(
                        audio
                    ) else onClick()
                },
                onLongClick = { interfaceViewModel.activatePressed(true) })
            .background(Color.Black.copy(alpha = 0.5f))
    } else {
        Modifier
            .height(70.dp)
            .combinedClickable(
                onClick = {
                    if (interfaceViewModel.isPress) interfaceViewModel.setElementsSelected(
                        audio
                    ) else onClick()
                },
                onLongClick = { interfaceViewModel.activatePressed(true) })
            .fillMaxWidth()
    }
    return modifier
}

@Composable
fun BoxData(
    viewModel: PlaybackViewModel,
    audio: AudioFile,
    isPlaylist: Boolean = false,
    namePlaylist: String? = null,
    onClick: () -> Unit
) {
    val audioPlaying by viewModel.audioPlaying.observeAsState(null)
    val modifier = handleModifierBoxData(audio, audioPlaying, onClick)
    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 5.dp)
        ) {
        Spacer(Modifier.height(10.dp))
            ContentBoxData(audio, isPlaylist, namePlaylist)
        }
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
fun OptionMusic(audio: AudioFile) {
    var isSelectOptions by remember { mutableStateOf(true) }
    if (isSelectOptions) {
        Box(
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(5.dp)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Dialog(
                onDismissRequest = { isSelectOptions = false }) {
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
                            text = "Agregar musica a playlist:",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                    Spacer(Modifier.height(13.dp))
                    PlaylistSelect(audio.contentUri, audio.name)
                }
            }
        }
    }
}

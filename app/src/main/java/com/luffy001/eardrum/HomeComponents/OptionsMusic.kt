package com.luffy001.eardrum.HomeComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.luffy001.eardrum.Pages.PlaylistSelect
import com.luffy001.eardrum.PlayerComponents.msToTime
import com.luffy001.eardrum.R
import com.luffy001.eardrum.lib.AudioFile
import com.luffy001.eardrum.lib.imageFromPath
import com.luffy001.eardrum.screens.MenuMusicPlaylist
import com.luffy001.eardrum.service.PlaybackViewModel


@Composable
fun BoxData(
    viewModel: PlaybackViewModel,
    audio: AudioFile,
    isPlaylist: Boolean = false,
    namePlaylist: String? = null,
    onClick: () -> Unit
) {
    val image = imageFromPath(audio.contentUri)
    val audioPlaying by viewModel.audioPlaying.observeAsState(null)
    val totalWidth = LocalConfiguration.current.screenWidthDp.dp
    val modifier = if (audio.contentUri == (audioPlaying?.contentUri ?: "")) {
        Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable(onClick = { onClick() })
            .background(Color.Black.copy(alpha = 0.5f))
    } else {
        Modifier
            .height(70.dp)
            .clickable(onClick = { onClick() })
            .fillMaxWidth()
    }
    val painter = painterResource(R.drawable.ic_logosimple)
    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 5.dp)
        ) {
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
            MenuMusicPlaylist(isPlaylist, audio, namePlaylist)
        }
    }
    Spacer(Modifier.height(10.dp))
}

@Composable
fun OptionMusic(audio: AudioFile) {
    var isSelectOptions by remember { mutableStateOf(true) }
    if (isSelectOptions) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Popup(
                properties = PopupProperties(), onDismissRequest = { isSelectOptions = false }) {
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
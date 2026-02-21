package com.luffy001.eardrum.Animations

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luffy001.eardrum.R
import com.luffy001.eardrum.ViewModels.musicPlaylist
import com.luffy001.eardrum.ViewModels.uiModel

@Composable
fun RandomAnimation(isRandom: Boolean) {
    val randomIcon = painterResource(R.drawable.ic_random)
    val noRandomIcon = painterResource(R.drawable.ic_order_playlist)
    AnimatedContent(
        targetState = isRandom, transitionSpec = {
            (fadeIn(animationSpec = tween(500)) + scaleIn()).togetherWith(
                fadeOut(animationSpec = tween(500)) + scaleOut()
            )
        }, label = "RandomAnimation"
    ) { state ->
        Icon(
            painter = if (state) noRandomIcon else randomIcon,
            tint = Color.White,
            modifier = Modifier
                .size(35.dp)
                .padding(start = 10.dp),
            contentDescription = "play"
        )
    }
}

@Composable
fun SearchAnimationTopBar(isPlaylist: Boolean, name: String = "") {
    val searchIcon = painterResource(R.drawable.ic_search_icon)
    var nameMusic by remember { mutableStateOf("") }
    val arrowBack = painterResource(R.drawable.ic_remove_x)
    val totalWidth = LocalConfiguration.current.screenWidthDp.dp
    val colorTitle = if (isPlaylist) Color.Yellow else Color.White
    var isExpanded by remember { mutableStateOf(false) }
    AnimatedContent(
        targetState = isExpanded, transitionSpec = {
            (fadeIn(animationSpec = tween(600)) + scaleIn()).togetherWith(
                fadeOut(animationSpec = tween(600)) + scaleOut()
            )
        }, label = "SearchAnimationTopBar"
    ) { state ->
        Row(
            horizontalArrangement = if (!isExpanded) Arrangement.SpaceBetween else Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(0.dp)
                .fillMaxWidth()
        ) {

            if (!state) {
                Text(text = name, color = colorTitle)
//                        Button(onClick = { recargar lista de app
//                            uiModel.setAudioList(loadFilesAudio(MyApplication.instance.contentResolver))
//                            Log.i("reload", "lista de musica recargada")
//                        }) { Text(text = "recargar")}
                IconButton(onClick = { isExpanded = true }) {
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
                    isExpanded = false
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
    }
}
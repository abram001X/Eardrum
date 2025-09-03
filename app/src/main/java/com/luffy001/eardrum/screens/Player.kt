package com.luffy001.eardrum.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luffy001.eardrum.TopBar
import com.luffy001.eardrum.lib.AudioFile
import com.luffy001.eardrum.lib.audioList
import com.luffy001.eardrum.lib.imageFromPath
import com.luffy001.eardrum.R

lateinit var audioFile: AudioFile

@Composable
fun InitPlayerApp(id: Long?) {
    audioFile = audioList.filter { audio -> audio.id.equals(id) }[0]
    PlayerApp()
}

@Composable
fun ImagePlayer() {
    val modifier = Modifier
        .height(400.dp)
        .width(350.dp)
        .padding(10.dp)
    val imageMusic = painterResource(R.drawable.ic_logosimple)
    val image = imageFromPath(audioFile.contentUri)
    if (image !== null) {
        Image(bitmap = image, contentDescription = audioFile.name, modifier)
    } else Image(painter = imageMusic, contentDescription = audioFile.name, modifier)
}

@Composable
fun PlayerApp() {
    val tint = Color.White
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImagePlayer()
        Spacer(Modifier.height(30.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.Center, Alignment.CenterVertically) {
            Text(text = audioFile.name, color = tint, fontSize = 20.sp, maxLines = 1)
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(200.dp),
            Arrangement.Center,
            Alignment.CenterVertically
        ) {
            HandleMusic()
        }
    }
}

@Composable
fun HandleMusic() {
    var isPlaying by remember { mutableStateOf(true) }
    Log.i("pause", isPlaying.toString())
    val tint = Color.White
    val modifierSize = Modifier.size(100.dp)
    val leftImage = painterResource(R.drawable.ic_left_arrow)
    val playImage = painterResource(R.drawable.ic_play)
    val rightImage = painterResource(R.drawable.ic_right_arrow)
    val pauseImage = painterResource(R.drawable.ic_pause)

    Icon(leftImage, "Left", modifierSize, tint)
    Icon(
        painter = if (isPlaying) pauseImage else playImage,
        contentDescription = "PauseOrPlay",
        Modifier
            .size(100.dp)
            .clickable(onClick = {
                playerController.playAndStop()
                isPlaying = !isPlaying
            }),
        tint
    )

    Icon(
        rightImage,
        "Right",
        Modifier
            .size(100.dp)
            .clickable(onClick = { playerController.nextAudio() }),
        tint
    )


}


@Preview(showSystemUi = true)
@Composable
fun ReproductorPreview() {
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = { TopBar() }) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            InitPlayerApp(34)
        }
    }
}
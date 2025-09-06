package com.luffy001.eardrum.lib

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.luffy001.eardrum.MyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(uriList: List<AudioFile>, position: Int = 0) : ViewModel() {
    var playList by mutableStateOf(uriList)
        private set
    val player = ExoPlayer.Builder(MyApplication.instance).build()
    var isPlaying by mutableStateOf(true)
        private set
    val indexItem = position
    var audioPlaying by mutableStateOf(playList[position])
        private set

    var currentPosition by mutableFloatStateOf(player.currentPosition.toFloat())
        private set

    fun prepareMedia() {
        playList.forEach { audioFile ->
            val mediaItem = MediaItem.fromUri(audioFile.contentUri)
            player.addMediaItem(mediaItem)
        }
        player.prepare()
        player.seekTo(indexItem, 0L)
        playerController.playAndStop()
    }

    fun playAndStop() {
        if (player.isPlaying) {
            player.pause()
        } else player.play()
        isPlaying = player.isPlaying
    }

    fun previousNextAudio(isNext: Boolean) {
        if (isNext) player.seekToNext()
        else player.seekToPrevious()
        audioPlaying = playList[player.currentMediaItemIndex]
    }

    fun runAudio() {
        val job = CoroutineScope(Dispatchers.Main).launch {
            while (isPlaying) {
                if (audioPlaying !== playList[player.currentMediaItemIndex]) {
                    audioPlaying = playList[player.currentMediaItemIndex]
                }
                currentPosition = player.currentPosition.toFloat()
                delay(1000)
            }
        }
        if (!isPlaying) job.cancel()
    }

    fun setPosition(position: Long) {
        Log.i("pos", position.toString())
        player.seekTo(position)
        currentPosition = player.currentPosition.toFloat()
    }
}

var playerController by mutableStateOf(PlayerViewModel(audioList, 0))
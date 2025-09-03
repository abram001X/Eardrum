package com.luffy001.eardrum.lib

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.luffy001.eardrum.MyApplication

class PlayerController(uriList: List<AudioFile>) {
    val playList = uriList
    val player = ExoPlayer.Builder(MyApplication.instance).build()

    fun prepareMedia() {
        playList.forEach { audioFile ->
            val mediaItem = MediaItem.fromUri(audioFile.contentUri)
            player.addMediaItem(mediaItem)
        }
        player.prepare()

    }

    fun playAndStop() {
        if (player.isPlaying) {
            player.pause()
        } else player.play()
    }

    fun nextAudio(){
        player.seekToNext()
    }

}
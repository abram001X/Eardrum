package com.luffy001.eardrum.ViewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.luffy001.eardrum.lib.AudioFile
import kotlin.collections.mutableListOf

class UiModel : ViewModel() {
    var musicsList by mutableStateOf(mutableListOf<AudioFile>())
        private set
    private lateinit var listAudioMedia: List<AudioFile>
    fun setAudioList(list: List<AudioFile>) {
        Log.i("list", list.toString())
        musicsList = list.toMutableList()
        listAudioMedia = list
    }

    fun searchMusicByName(nameMusic: String) {
        if (this::listAudioMedia.isInitialized) {
            if (nameMusic.trimEnd() == "") {
                musicsList = listAudioMedia.toMutableList()
            }
            val listSearch =
                listAudioMedia.filter { it -> it.name.lowercase().contains(nameMusic.lowercase()) }
            musicsList = listSearch.toMutableList()
        }
    }
}

val uiModel by mutableStateOf(UiModel())
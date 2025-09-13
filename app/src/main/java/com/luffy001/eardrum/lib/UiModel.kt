package com.luffy001.eardrum.lib

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class UiModel() : ViewModel() {
    var musicsList by mutableStateOf(mutableListOf<AudioFile>())
    fun setAudioList(list: List<AudioFile>) {
        musicsList = list.toMutableList()
    }
}

val uiModel by mutableStateOf(UiModel())
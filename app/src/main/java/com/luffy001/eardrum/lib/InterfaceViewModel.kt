package com.luffy001.eardrum.lib

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class InterfaceViewModel : ViewModel() {
    var isPress by mutableStateOf(false)
        private set
    var elementsSelected by mutableStateOf<List<AudioFile>>(emptyList())
        private set
    private var listElements = mutableListOf<AudioFile>()
    fun setElementsSelected(element: AudioFile) {
        listElements.add(element)
        elementsSelected = listElements
        Log.i("list", elementsSelected.toString())
    }

    fun activatePressed(isPressed: Boolean = true) {
        isPress = isPressed
        if (!isPressed) {
            listElements = mutableListOf<AudioFile>()
            elementsSelected = mutableListOf<AudioFile>()
        }
    }

    fun removeMusicSelect(audioFile: AudioFile) {
        listElements.remove(audioFile)
        elementsSelected = listElements
        if (listElements.isEmpty()) activatePressed(false)
        Log.i("list", elementsSelected.toString())
    }
}

val interfaceViewModel = InterfaceViewModel()
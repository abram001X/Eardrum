package com.luffy001.eardrum.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.luffy001.eardrum.lib.AudioFile

class InterfaceViewModel : ViewModel() {
    var isPress by mutableStateOf(false)
        private set
    var elementsSelected by mutableStateOf<List<AudioFile>>(emptyList())
        private set
    private var listElements = mutableListOf<AudioFile>()

    var countElements by mutableIntStateOf(0)
        private set

    fun setElementsSelected(element: AudioFile) {
        listElements.add(element)
        elementsSelected = listElements
        countElements = elementsSelected.size
    }

    fun activatePressed(isPressed: Boolean = true) {
        isPress = isPressed
        if (!isPressed) {
            listElements = mutableListOf<AudioFile>()
            elementsSelected = mutableListOf<AudioFile>()
            countElements = elementsSelected.size
        }
    }

    fun removeMusicSelect(audioFile: AudioFile) {
        listElements.remove(audioFile)
        elementsSelected = listElements
        if (listElements.isEmpty()) activatePressed(false)
        countElements = elementsSelected.size
    }
}

val interfaceViewModel = InterfaceViewModel()
package com.luffy001.eardrum.ViewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luffy001.eardrum.lib.AudioFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import kotlin.collections.mutableListOf

class UiModel(private val repository:DatastorePreferences) : ViewModel() {
    private val _items = MutableStateFlow<List<AudioFile>>(emptyList())
    val items: StateFlow<List<AudioFile>> = _items.asStateFlow()
    private val _currentOrder = MutableStateFlow("date")

    var listAudioMedia: List<AudioFile> = emptyList()
        private set

    init {
        viewModelScope.launch {
            repository.listOrder.collect { orderString->
                _currentOrder.value = orderString
                sortItems(orderString)
            }
        }
    }

    fun sortItems(orderString: String){
        var list = listAudioMedia
        if(orderString == "date"){
            list = listAudioMedia.sortedBy { it.date }.reversed()
        }
        if(orderString == "abc"){
            list = listAudioMedia.sortedBy { it.name }
        }
        listAudioMedia = list
        _items.value = list
    }

    fun onOrderList(newOrder: String){
        _currentOrder.value = newOrder
        viewModelScope.launch {
            repository.saveListOrder(newOrder)
        }
    }
    fun setAudioList(list: List<AudioFile>) {
        listAudioMedia = list
        viewModelScope.launch {
            sortItems(_currentOrder.value)
        }
        Log.i("list", list.toString())
    }

    fun searchMusicByName(nameMusic: String) {
        if (listAudioMedia.isNotEmpty()) {
            if (nameMusic.trimEnd() == "") {
                _items.value = listAudioMedia.toMutableList()
            }
            val listSearch =
                listAudioMedia.filter { it -> it.name.lowercase().contains(nameMusic.lowercase()) }
            _items.value = listSearch
        }
    }

}

val uiModel by mutableStateOf(UiModel(DatastorePreferences()))
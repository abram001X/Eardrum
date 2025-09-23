package com.luffy001.eardrum.service

import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.luffy001.eardrum.MyApplication
import com.luffy001.eardrum.lib.AudioFile
import com.luffy001.eardrum.lib.loadFilesAudio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.forEach

class   PlaybackViewModel : ViewModel() {
    private lateinit var controller: MediaController

    private val _indexItem = MutableLiveData(0)
    val indexItem: LiveData<Int> = _indexItem

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _playList = MutableLiveData(emptyList<AudioFile>())
    val playList: LiveData<List<AudioFile>> = _playList

    private val _audioPlaying = MutableLiveData<AudioFile?>(null)
    val audioPlaying: LiveData<AudioFile?> = _audioPlaying
    private val _currentPosition = MutableLiveData(0f)
    val currentPosition: LiveData<Float> = _currentPosition

    private val _isRandom = MutableLiveData(false)
    val isRandom: LiveData<Boolean> = _isRandom
    fun init(sessionToken: SessionToken) {
        try {
            val controllerFuture =
                MediaController.Builder(MyApplication.instance, sessionToken).buildAsync()
            controllerFuture.addListener({
                controller = controllerFuture.get()
                controller.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _isPlaying.postValue(isPlaying)
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        super.onMediaItemTransition(mediaItem, reason)
                        _playList.value?.let { it ->
                            _playList.value?.let { it -> _audioPlaying.postValue(it[controller.currentMediaItemIndex]) }
                        }
                    }
                })
            }, ContextCompat.getMainExecutor(MyApplication.instance))
        } catch (e: Exception) {
            Log.i("error", "Error: ${e.message.toString()}")
        }

    }
    fun setPlaylist(listAudio: List<AudioFile>, indexItem: Int) {
        _playList.postValue(listAudio)
        _indexItem.postValue(indexItem)
    }

    fun addMediaToPlaylist(listAudio: List<AudioFile>) {
        val listAdd = mutableListOf<AudioFile>()
        if (_playList.value !== null) {
            listAdd.addAll(_playList.value?: emptyList())
            listAdd.addAll(listAudio)
            _playList.postValue(listAdd)
        }
        val listMediaUri = listAudio.map { it -> MediaItem.fromUri(it.contentUri) }
        controller.addMediaItems(listMediaUri)
    }

    fun prepareMedia() {
        val listUri = _playList.value?.let { it.map { it -> MediaItem.fromUri(it.contentUri) } }
        try {
            if (_playList.value != null && listUri != null) {
                _playList.value?.let { controller.setMediaItems(listUri.toMutableList(), true) }
                if (_playList.value.isEmpty()) {
                    controller.prepare()
                    if (_isRandom.value == true) controller.shuffleModeEnabled = true
                }
                controller.seekTo(_indexItem.value ?: 0, 0L)
            }
            _playList.value?.let { it -> _audioPlaying.postValue(it[controller.currentMediaItemIndex]) }
            playAndStop()
        } catch (e: Exception) {
            Log.i("Error", "Error: ${e.message}")
        }
    }

    fun playAndStop() {
        if (controller.isPlaying) {
            controller.pause()
        } else controller.play()
        _isPlaying.postValue(controller.isPlaying)
    }

    fun previousNextAudio(isNext: Boolean) {
        if (isNext) controller.seekToNext()
        else controller.seekToPrevious()
        _playList.value?.let { it -> _audioPlaying.postValue(it[controller.currentMediaItemIndex]) }
    }

    fun runAudio() {
        val job = CoroutineScope(Dispatchers.Main).launch {
            while (controller.isPlaying) {
                _currentPosition.postValue(controller.currentPosition.toFloat())
                delay(1000)
            }
        }
        if (!controller.isPlaying) job.cancel()
    }

    fun setPosition(position: Long) {
        Log.i("pos", position.toString())
        controller.seekTo(position)
        _currentPosition.postValue(controller.currentPosition.toFloat())
    }

    fun activeRandomMode() {
        _isRandom.value?.let { controller.shuffleModeEnabled = !it }
        _isRandom.value?.let { _isRandom.postValue(!it) }
    }

    fun changeMusic(position: Int = 0) {
        controller.seekTo(position, 0L)
        playAndStop()
    }

    override fun onCleared() {
        super.onCleared()
        controller.release()
    }

}


package com.luffy001.eardrum.service

import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.luffy001.eardrum.MyApplication
import com.luffy001.eardrum.ViewModels.DatastorePreferences
import com.luffy001.eardrum.ViewModels.uiModel
import com.luffy001.eardrum.lib.AudioFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaybackViewModel(private val repository: DatastorePreferences) : ViewModel() {
    private lateinit var controller: MediaController
    private val _indexItem = MutableLiveData(0)

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _playList = MutableLiveData(emptyList<AudioFile>())
    val playList: LiveData<List<AudioFile>> = _playList

    private val _audioPlaying = MutableLiveData<AudioFile?>(null)
    val audioPlaying: LiveData<AudioFile?> = _audioPlaying
    private val _currentPosition = MutableLiveData(0f)
    val currentPosition: LiveData<Float> = _currentPosition

    private val _processAudio = MutableLiveData(0f)
    val processAudio: LiveData<Float> = _processAudio
    private val _isRandom = MutableStateFlow<Boolean>(false)
    val isRandom: StateFlow<Boolean> = _isRandom.asStateFlow()

    init {
        viewModelScope.launch {
            repository.randomMode.collect { random ->
                _isRandom.value = random.toBoolean()
            }
        }
    }

    fun init(sessionToken: SessionToken) {
        try {
            val controllerFuture =
                MediaController.Builder(MyApplication.instance, sessionToken).buildAsync()
            controllerFuture.addListener({
                controller = controllerFuture.get()
                getInitInfoPlayer()

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

    fun getInitInfoPlayer() {
        if (::controller.isInitialized) {
            if (controller.playbackState == Player.STATE_READY) {
                val listPlaylist = mutableListOf<AudioFile>()
                for (i in 0 until controller.mediaItemCount) {
                    val mediaItem = controller.getMediaItemAt(i)
                    val name = mediaItem.mediaMetadata.title
                    val item = uiModel.listAudioMedia.find { it ->
                        it.name == name.toString()
                    }
                    Log.i("nameA", item.toString())
                    listPlaylist.add(item ?: uiModel.listAudioMedia[0])
                }
                val mediaItem = controller.mediaMetadata
                val audioFile =
                    uiModel.listAudioMedia.find { it -> it.name == mediaItem.title.toString() }
                _isPlaying.postValue(controller.isPlaying)
                _audioPlaying.postValue(audioFile)
                _playList.postValue(listPlaylist.toList())
            }
        }
    }

    fun setPlaylist(listAudio: List<AudioFile>, indexItem: Int) {
        _playList.postValue(listAudio)
        _indexItem.postValue(indexItem)
    }

    fun addMediaToPlaylist(listAudio: List<AudioFile>) {
        val listAdd = mutableListOf<AudioFile>()
        if (_playList.value !== null) {
            listAdd.addAll(_playList.value ?: emptyList())
            listAdd.addAll(listAudio)
            _playList.postValue(listAdd)
        }
        val listMediaUri = listAudio.map { it -> MediaItem.fromUri(it.contentUri) }
        controller.addMediaItems(listMediaUri)
    }

    fun prepareMedia() {
        val listUri = _playList.value?.let {
            it.map { it ->
                MediaItem.Builder()
                    .setUri(it.contentUri).setMediaId(it.id.toString())
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(it.name)
                            .setDurationMs(it.duration.toLong()).build()
                    )
                    .build()

            }
        }
        try {
            if (_playList.value != null && listUri != null) {
                _playList.value?.let {
                    controller.setMediaItems(listUri.toMutableList(), true)
                }
                if (_playList.value.isEmpty()) {
                    controller.repeatMode = Player.REPEAT_MODE_ALL
                    controller.prepare()
                    if (_isRandom.value == true) controller.shuffleModeEnabled = true
                }
            }
            controller.repeatMode = Player.REPEAT_MODE_ALL
            controller.seekTo(_indexItem.value ?: 0, 0L)
            _playList.value?.let { it -> _audioPlaying.postValue(it[controller.currentMediaItemIndex]) }
            _processAudio.postValue(0f)
            _currentPosition.postValue(0f)
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
        _processAudio.postValue(0f)
    }

    fun runAudio() {
        val job = CoroutineScope(Dispatchers.Main).launch {
            while (controller.isPlaying) {
                _currentPosition.postValue(controller.currentPosition.toFloat())
                val totalDuration = _audioPlaying.value?.duration?.toFloat() ?: 0f
                _processAudio.postValue(controller.currentPosition / totalDuration)
                //porcentaje de pantalla : process * totalWidth
                //porcentaje de audio proces : (currentAudio / totalDuration )
                delay(1000)

            }
        }
        if (!controller.isPlaying) job.cancel()
    }

    fun setPosition(position: Long) {
        val totalDuration = _audioPlaying.value?.duration?.toFloat() ?: 0f
        Log.i("pos", position.toString())
        controller.seekTo(position)
        _currentPosition.postValue(controller.currentPosition.toFloat())
        _processAudio.postValue(controller.currentPosition / totalDuration)
    }

    fun activeRandomMode() {
        if (::controller.isInitialized) {
            controller.shuffleModeEnabled = !_isRandom.value
        }
        _isRandom.value = !_isRandom.value
        viewModelScope.launch {
            repository.saveRandomMode(_isRandom.value)
        }
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


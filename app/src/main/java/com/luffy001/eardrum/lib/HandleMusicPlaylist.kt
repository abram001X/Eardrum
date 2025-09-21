package com.luffy001.eardrum.lib

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.luffy001.eardrum.MyApplication
import java.io.File
import java.io.FileOutputStream

class HandleMusicPlaylist() : ViewModel() {
    val internalDir = MyApplication.instance.filesDir
    val playlists = File(internalDir.absolutePath, "playlists")
    var playlistsModel by mutableStateOf((emptyArray<String>()))
        private set
    var listMusicsModel by mutableStateOf<List<AudioFile>>(emptyList())
        private set

    fun addMusicToPlaylist(namePlaylist: String, listAudio: List<AudioFile>) {
        listAudio.forEach { it ->
            val uri = it.contentUri
            val inputStream = MyApplication.instance.contentResolver.openInputStream(uri)
            val internalPlaylists = File(playlists.absolutePath, namePlaylist)
            val newMusic = File(internalPlaylists.absolutePath, it.name)
            val outputStream = FileOutputStream(newMusic)
            try {
                val buffer = ByteArray(1024)
                var length: Int
                if (inputStream !== null) {
                    while (inputStream.read(buffer).also { length = it } > 0) {
                        outputStream.write(buffer, 0, length)
                    }
                }

            } catch (e: Exception) {
                Log.i("play", e.message.toString())
            } finally {
                inputStream?.close()
                outputStream.close()
            }
        }
    }

    fun getMusicsPlaylist(namePlaylist: String) {
        try {
            val musicsList = File(playlists.absolutePath, namePlaylist)
            if (musicsList.list() !== null) {
                playlistsModel = musicsList.list()
                loadFilesPlaylist(musicsList)
            }
        } catch (e: Exception) {
            Log.i("play", e.message.toString())
        }
    }

    fun loadFilesPlaylist(musicsList: File) {
        val retriever = MediaMetadataRetriever()
        try {
            val listsAudio = mutableListOf<AudioFile>()
            val fileList = musicsList.listFiles()
            for (index in fileList.indices) {
                retriever.setDataSource(fileList[index].absolutePath)
                val id = index.toLong()
                val duration =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?.toInt() ?: 0
                val name =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE).toString()

                val date =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)?.toInt()
                        ?: 0
                val contentUri = Uri.fromFile(fileList[index])
                val audio = AudioFile(id, name, duration, contentUri, date)
                listsAudio.add(audio)
            }
            listMusicsModel = listsAudio
        } catch (e: Exception) {
            Log.i("play", "Error: ${e.message.toString()}")
        } finally {
            retriever.release()
        }

    }

    fun removeMusicFromPlaylists(namePlaylist: String, listAudio: List<AudioFile>) {
        try {
            val listRemoved = mutableListOf<AudioFile>()
            listRemoved.addAll(listMusicsModel)
            listAudio.forEach { it ->
                val playlistFolder = File(playlists.absolutePath, namePlaylist)
                val musicFile = File(playlistFolder.absolutePath, it.name)
                if (musicFile.delete()) {
                    Log.i("play", "${musicFile}:  file deleted")
                } else Log.i("play", "${musicFile}:  file error")
            }
            listRemoved.removeAll(listAudio)
            listMusicsModel = listRemoved
        } catch (e: Exception) {
            Log.i("play", "Error: ${e.message}")
        }

    }

    fun setPlaylistModel(list: List<AudioFile>) {
        listMusicsModel = list.toMutableList()
    }
}

val musicPlaylist by mutableStateOf(HandleMusicPlaylist())

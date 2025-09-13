package com.luffy001.eardrum.lib

import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.luffy001.eardrum.MyApplication
import com.luffy001.eardrum.network.Data
import java.io.File
import kotlin.collections.List

class HandlePlaylists() : ViewModel() {

    val internalDir = MyApplication.instance.filesDir
    val playlists = File(internalDir.absolutePath, "playlists")
    var playlistsModel by mutableStateOf((emptyArray<String>()))
    var message by mutableStateOf("")
    fun getPlaylists() {
        try {
            val playlists = File(internalDir.absolutePath, "playlists")
            val internalPlaylists = File(playlists.absolutePath).list()
            if (internalPlaylists !== null) {
                playlistsModel = internalPlaylists
            }
        } catch (e: Exception) {
            Log.i("play", e.message.toString())
        }
    }

    fun createPlaylist(name: String = "") {
        try {
            val playlists = File(internalDir.absolutePath, "playlists")
            if (!playlists.exists()) {
                playlists.mkdir()
            }
            val internalPlaylists = File(playlists.absolutePath, name)
            if (!internalPlaylists.exists()) {
                internalPlaylists.mkdir()
            } else message = "Nombre ya utilizado"
            Log.i("play", internalPlaylists.toString())

        } catch (e: Exception) {
            Log.i("play", e.message.toString())
        }
    }
    fun removePlaylist(namePlaylist: String) {
        try {
            val folderToDelete = File(playlists.absolutePath, namePlaylist)
            val listFiles = folderToDelete.listFiles()
            if (listFiles != null) {
                for (file in listFiles) {
                    file.delete()
                }
            }
            folderToDelete.delete()
            Log.i("play", "$namePlaylist, borrado")

        } catch (e: Exception) {
            Log.i("play", "Error: ${e.message}")
        }
    }

}

var playlistController by mutableStateOf(HandlePlaylists())
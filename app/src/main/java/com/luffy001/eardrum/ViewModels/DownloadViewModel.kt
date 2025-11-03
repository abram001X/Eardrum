package com.luffy001.eardrum.ViewModels

import android.app.DownloadManager
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luffy001.eardrum.MyApplication
import com.luffy001.eardrum.network.Data
import com.luffy001.eardrum.network.DownloadRemote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URI

class DownloadViewModel : ViewModel() {
    private val _resultSearch = MutableLiveData(emptyList<Data>())
    val resultSearch: LiveData<List<Data>> = _resultSearch
    private val _downloadProgress = MutableLiveData(0)
    val downloadProgress: LiveData<Int> = _downloadProgress
    fun changeResultSearch(listVideo: List<Data>) {
        _resultSearch.postValue(listVideo)
    }
    fun downloadAudio(audioUrl: DownloadRemote) {
        try {
            val request =
                DownloadManager.Request(Uri.parse(audioUrl.link)).setTitle(audioUrl.title)
                    .setDescription("Descargando archivo...")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_MUSIC,
                        audioUrl.title
                    )
            val downloadManager =
                MyApplication.instance.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadId = downloadManager.enqueue(request)
            downloadProcess(downloadId, downloadManager)
        } catch (e: Exception) {
            Log.i("error", "error: ${e.message}")
        }
    }

    private fun downloadProcess(downloadId: Long, downloadManager: DownloadManager) {
        viewModelScope.launch(Dispatchers.IO) {
            var isDownloading = true
            while (isDownloading) {
                val query = DownloadManager.Query().setFilterById(downloadId)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    val status = cursor.getInt(columnIndex)

                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            isDownloading = false
                            _downloadProgress.postValue(100)
                        }

                        DownloadManager.STATUS_FAILED -> {
                            isDownloading = false
                            _downloadProgress.postValue(-1)
                        }

                        DownloadManager.STATUS_PENDING,
                        DownloadManager.STATUS_RUNNING -> {
                            val bytesDownloadedIndex =
                                cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                            val totalBytesIndex =
                                cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)

                            val bytesDownloaded = cursor.getLong(bytesDownloadedIndex)
                            val totalBytes = cursor.getLong(totalBytesIndex)

                            if (totalBytes > 0) {
                                val progress = (bytesDownloaded * 100 / totalBytes).toInt()
                                _downloadProgress.postValue(progress)
                            }
                        }
                    }
                }
                delay(1000)
                cursor.close()
            }
        }
    }

}

val downloadViewModel by mutableStateOf(DownloadViewModel())
package com.luffy001.eardrum.lib

import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.luffy001.eardrum.MyApplication
import java.io.File

data class AudioFile(
    val id: Long,
    val name: String,
    val duration: Int,
    val contentUri: Uri,
    val date: Int,
)

val audioList = mutableListOf<AudioFile>()
fun loadFilesAudio(resolver: ContentResolver): MutableList<AudioFile> {
    audioList.clear()
    val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Audio.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL
        )
    } else {
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }
    val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.DATE_ADDED,
        MediaStore.Audio.Media.RELATIVE_PATH
    )
    val query = resolver.query(
        collection, projection, null, null, sortOrder
    )
    query?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val name = cursor.getString(nameColumn)
            val duration = cursor.getInt(durationColumn)
            val contentUri: Uri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                id
            )
            val date = cursor.getInt(dateColumn)
            val audio = AudioFile(id, name, duration, contentUri, date)
            audioList.add(audio)
        }
    }
    return audioList
}

fun forceMediaScan(context: Context, directoryName: String) {
    val file = Environment.getExternalStoragePublicDirectory(directoryName)
    if (file.isDirectory) {
        val filePaths = file.listFiles()?.map { it.absolutePath }?.toTypedArray()
        if (filePaths.isNullOrEmpty()) return
        MediaScannerConnection.scanFile(
            context,
            filePaths,
            null,
            null
        )
    }
}

fun deleteAudio(fileUri: Uri){
    val contentResolver = MyApplication.instance.contentResolver
    try {
        contentResolver.delete(fileUri,null,null)
    } catch (securityException : SecurityException){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val recoverableSecurityException = securityException as? RecoverableSecurityException
                ?: throw securityException
            Log.i("remaudio", "Se requiere el permiso del usuario")
        }
    }
}

fun removeAudios(listUris: List<AudioFile>){
    val contentResolver = MyApplication.instance.contentResolver
    try {
        listUris.forEach { uri ->
            contentResolver.delete(uri.contentUri,null,null)
        }
    } catch (securityException : SecurityException){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val recoverableSecurityException = securityException as? RecoverableSecurityException
                ?: throw securityException
            Log.i("remaudio", "Se requiere el permiso del usuario")
        }
    }
}


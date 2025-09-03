package com.luffy001.eardrum.lib

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.luffy001.eardrum.MyApplication

@Composable
fun imageFromPath(uri: Uri?): ImageBitmap? {
    var thumbnail by remember { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(uri) {
        if(uri !== null ){
            val bitmap = getAudioThumbnailFromUri(uri)
            thumbnail = bitmap?.asImageBitmap()
        }
    }
    return thumbnail
}

private fun getAudioThumbnailFromUri(uri: Uri): Bitmap? {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(MyApplication.instance, uri)
        val embeddedPicture = retriever.embeddedPicture
        if (embeddedPicture != null) {
            BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.size)

        } else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        retriever.release()
        null
    }
}


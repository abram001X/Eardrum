package com.luffy001.eardrum.lib

import android.database.ContentObserver
import android.os.Handler
import android.util.Log
import com.luffy001.eardrum.MyApplication
import com.luffy001.eardrum.ViewModels.uiModel

class MediaStoreObserver (handler: Handler): ContentObserver(handler){
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        Log.i("MediaStoreObserver", "¡Cambio detectado en MediaStore!")

        scanForNewAudioFiles()
    }
    private fun scanForNewAudioFiles(){
        uiModel.setAudioList(loadFilesAudio(MyApplication.instance.contentResolver))
    }
}
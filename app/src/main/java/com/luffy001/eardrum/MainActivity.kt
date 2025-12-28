package com.luffy001.eardrum

import android.Manifest
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.media3.session.SessionToken
import com.luffy001.eardrum.ViewModels.DatastorePreferences
import com.luffy001.eardrum.lib.loadFilesAudio
import com.luffy001.eardrum.ViewModels.uiModel
import com.luffy001.eardrum.lib.forceMediaScan
import com.luffy001.eardrum.navigation.AppNavigation
import com.luffy001.eardrum.service.PlaybackService
import com.luffy001.eardrum.service.PlaybackViewModel
import com.luffy001.eardrum.ui.theme.EardrumTheme


class MainActivity : ComponentActivity() {
    private val REQUEST_CODE_AUDIO = 100
    private val viewModel: PlaybackViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PlaybackViewModel(DatastorePreferences()) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sessionToken = SessionToken(
            MyApplication.instance,
            ComponentName(MyApplication.instance, PlaybackService::class.java)
        )
        viewModel.init(sessionToken)
        setContent {
            checkAudioPermission()
            EardrumTheme {
                AppNavigation(viewModel)
            }
        }
    }

    private fun checkAudioPermission() { // verificar si los permisos están concedidos
        val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO}
        else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_MEDIA_AUDIO
            ).equals(PackageManager.PERMISSION_GRANTED)
        ) {
            uiModel.setAudioList(loadFilesAudio(contentResolver))
            uiModel.setPermission(true)
        } else {
             //el permiso no concedido
            ActivityCompat.requestPermissions(
                this, arrayOf(permissionToRequest), REQUEST_CODE_AUDIO
            )
            uiModel.setAudioList(mutableListOf())
            uiModel.setPermission(false)
        }
    }
    override fun onRequestPermissionsResult( // pedir permisos
        requestCode: Int, permissions: Array<out String?>, grantResults: IntArray, deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == REQUEST_CODE_AUDIO) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                //permiso concedido
                //Forzar scaneo de mediaStore
                uiModel.setPermission(true)
                forceMediaScan(MyApplication.instance, Environment.DIRECTORY_MUSIC)//
                forceMediaScan(MyApplication.instance, Environment.DIRECTORY_DOWNLOADS)//
                uiModel.setAudioList(loadFilesAudio(contentResolver))

            } else {
                uiModel.setPermission(false)
                uiModel.setAudioList(mutableListOf())
            }
        }
    }

}

package com.luffy001.eardrum

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.luffy001.eardrum.lib.AudioFile
import com.luffy001.eardrum.lib.loadFilesAudio
import com.luffy001.eardrum.navigation.AppNavigation
import com.luffy001.eardrum.ui.theme.EardrumTheme

lateinit var audioFiles: MutableList<AudioFile>

class MainActivity : ComponentActivity() {
    private val REQUEST_CODE_AUDIO = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            checkAudioPermission()
            EardrumTheme {
                        AppNavigation()
            }

        }
    }
    private fun checkAudioPermission() {

        if (!ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_AUDIO
            ).equals(PackageManager.PERMISSION_GRANTED)
        ) {
            // el permiso no concedido
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                REQUEST_CODE_AUDIO
            )
        } else {
            audioFiles = loadFilesAudio(contentResolver)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode.equals(REQUEST_CODE_AUDIO)) {
            if (grantResults.isNotEmpty() && grantResults[0].equals(PackageManager.PERMISSION_GRANTED)) {
                //permiso concedido
                audioFiles = loadFilesAudio(contentResolver)
            } else {
                audioFiles = mutableListOf()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(title = { Text(text = "Eardrum") })
}

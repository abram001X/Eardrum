package com.luffy001.eardrum

import android.Manifest
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.session.SessionToken
import com.luffy001.eardrum.lib.AudioFile
import com.luffy001.eardrum.lib.audioList
import com.luffy001.eardrum.lib.loadFilesAudio
import com.luffy001.eardrum.lib.uiModel
import com.luffy001.eardrum.navigation.AppNavigation
import com.luffy001.eardrum.service.PlaybackService
import com.luffy001.eardrum.service.PlaybackViewModel
import com.luffy001.eardrum.ui.theme.EardrumTheme

lateinit var audioFiles: MutableList<AudioFile>

class MainActivity : ComponentActivity() {
    private val REQUEST_CODE_AUDIO = 100
    private val viewModel: PlaybackViewModel by viewModels()
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

    private fun checkAudioPermission() {

        if (!ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_MEDIA_AUDIO
            ).equals(PackageManager.PERMISSION_GRANTED)
        ) {
            // el permiso no concedido
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_MEDIA_AUDIO), REQUEST_CODE_AUDIO
            )
        } else {

            audioFiles = uiModel.musicsList.ifEmpty {
                loadFilesAudio(
                    contentResolver
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String?>, grantResults: IntArray, deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == REQUEST_CODE_AUDIO) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permiso concedido
                audioFiles = uiModel.musicsList.ifEmpty {
                    loadFilesAudio(
                        contentResolver
                    )
                }
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

package com.luffy001.eardrum.service

import android.app.PendingIntent
import android.content.Intent
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.luffy001.eardrum.MainActivity
import com.luffy001.eardrum.MyApplication
import com.luffy001.eardrum.lib.MediaStoreObserver

class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private var mediaStoreObserver: MediaStoreObserver? = null

    fun registerObserver() { ////Para observar loas audios dentro del dispositivo
        // Registrar observer
        mediaStoreObserver = MediaStoreObserver(Handler())
        val audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        contentResolver.registerContentObserver(
            audioUri,
            true,
            mediaStoreObserver!!
        )
    }

    fun unregisterObserver() {
        mediaStoreObserver?.let {
            contentResolver.unregisterContentObserver(it)
        }
    }


    override fun onCreate() {
        super.onCreate()
        registerObserver()
        //redirijir a la app al darle click a la notification
        val intent = Intent(MyApplication.instance, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            MyApplication.instance,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player).setSessionActivity(pendingIntent).build()

    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        // Notificación de reproducción persistente quedando en primer plano
        if (mediaSession?.player?.playWhenReady == true || mediaSession?.player?.playbackState == Player.STATE_READY) {
            val keepServiceRunning = true
            if (keepServiceRunning) return
        }
        mediaSession?.player?.release()
        stopSelf()
    }

    override fun onDestroy() {
        // Liberar espacio a la hora de detener una canción
        super.onDestroy()
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        unregisterObserver()
        super.onDestroy()
    }

}
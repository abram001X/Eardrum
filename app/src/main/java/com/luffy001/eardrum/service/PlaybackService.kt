package com.luffy001.eardrum.service

import android.content.Intent
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.luffy001.eardrum.lib.MediaStoreObserver

class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private var mediaStoreObserver: MediaStoreObserver? = null
    companion object {
        const val CUSTOM_COMMAND_LIKE = "ACTION_LIKE"
    }
    fun registerObserver(){
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
        val player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player).build()

    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        // Notificación de reproducción persistente quedando en primer plano
        if(mediaSession?.player?.playWhenReady == true || mediaSession?.player?.playbackState == Player.STATE_READY){
            val keepServiceRunning = true
            if(keepServiceRunning) return
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
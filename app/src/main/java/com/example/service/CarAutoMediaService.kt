package com.example.service

import android.media.browse.MediaBrowser.MediaItem
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Bundle
import android.service.media.MediaBrowserService

class CarAutoMediaService : MediaBrowserService() {

    private var mediaSession: MediaSession? = null

    companion object {
        private const val MEDIA_ROOT_ID = "autodrive_root"
    }

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSession(this, "CarAutoMediaService").apply {
            setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS or MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS)
            setPlaybackState(
                PlaybackState.Builder()
                    .setActions(PlaybackState.ACTION_PLAY or PlaybackState.ACTION_PAUSE or PlaybackState.ACTION_SKIP_TO_NEXT or PlaybackState.ACTION_SKIP_TO_PREVIOUS)
                    .setState(PlaybackState.STATE_PLAYING, 0, 1.0f)
                    .build()
            )
            isActive = true
        }
        sessionToken = mediaSession?.sessionToken
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot {
        return BrowserRoot(MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaItem>>) {
        // Expose media items for Android Auto head unit display
        val mediaItems = mutableListOf<MediaItem>()
        result.sendResult(mediaItems)
    }

    override fun onDestroy() {
        mediaSession?.release()
        super.onDestroy()
    }
}

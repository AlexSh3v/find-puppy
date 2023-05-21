package com.alexsh3v.findpuppy.utils

import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams

class AudioPlayer(
    private val context: Context
): IAudioPlayer {

    val isPlaying: Boolean
        get() = mediaPlayer?.isPlaying ?: false

    private var mediaPlayer: MediaPlayer? = null

    override fun play(resourceId: Int, onPrepareCallback: ((MediaPlayer) -> Unit)?) {

        MediaPlayer.create(context, resourceId).apply {
            mediaPlayer = this
            onPrepareCallback?.invoke(this)
            start()
        }
    }

    override fun stop() {

        mediaPlayer?.apply {
            stop()
            release()
        }

        mediaPlayer = null
    }

    override fun setSpeed(x: Float) {
        mediaPlayer?.playbackParams = PlaybackParams().setSpeed(x)
    }
}
package com.alexsh3v.findpuppy.utils

import android.content.Context
import android.media.MediaPlayer

class AudioPlayer(
    private val context: Context
): IAudioPlayer {

    val isPlaying: Boolean
        get() = mediaPlayer?.isPlaying ?: false

    private var mediaPlayer: MediaPlayer? = null

    override fun play(resourceId: Int) {

        MediaPlayer.create(context, resourceId).apply {
            mediaPlayer = this
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
}
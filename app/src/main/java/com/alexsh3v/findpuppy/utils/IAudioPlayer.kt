package com.alexsh3v.findpuppy.utils

import android.media.MediaPlayer

interface IAudioPlayer {
    fun play(resourceId: Int, onPrepareCallback: ((MediaPlayer) -> Unit)? = null)
    fun stop()
    fun setSpeed(x: Float = 1f)
}
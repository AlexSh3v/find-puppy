package com.alexsh3v.findpuppy.game

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class Timer(private val scope: CoroutineScope) {

    private var isActivated = false
    private var timeMillis = 0L
    private var lastTimestamp = 0L

    private val valueState = MutableStateFlow(0)
    val valueSeconds: Flow<Int>
        get() = valueState

    fun start() {

        if (isActivated)
            return

        scope.launch {
            isActivated = true
            lastTimestamp = System.currentTimeMillis()
            while (isActivated) {
                delay(10)
                timeMillis += System.currentTimeMillis() - lastTimestamp
                lastTimestamp = System.currentTimeMillis()
                valueState.value = (timeMillis / 1000).toInt()
            }
        }
    }

    fun pause() {
        isActivated = false
    }

    fun reset() {
        isActivated = false
        timeMillis = 0L
        lastTimestamp = 0L
    }

    companion object {

        fun prettify(value: Int): String {

            var minutes = (value.toInt() / 60).toString()
            var seconds = (value.toInt() % 60).toString()

            if (minutes.length == 1) minutes = "0$minutes"
            if (seconds.length == 1) seconds = "0$seconds"

            return "$minutes:$seconds"

        }

    }

}

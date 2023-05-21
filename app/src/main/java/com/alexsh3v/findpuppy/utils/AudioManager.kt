package com.alexsh3v.findpuppy.utils

import android.content.Context
import android.media.PlaybackParams
import android.util.Log
import com.alexsh3v.findpuppy.FindPuppyGame
import com.alexsh3v.findpuppy.R
import com.alexsh3v.findpuppy.game.Tile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class AudioManager(
    private val context: Context,
    private val scope: CoroutineScope
) {

    val audioQueue = ArrayDeque<AudioPlayer>()

    init {
        scope.launch {
            var audio: AudioPlayer
            while (true) {

                if (audioQueue.isEmpty()) {
                    delay(250)
                    continue
                }

                audio = audioQueue.removeFirst()

                if (audio.isPlaying) {
                    audioQueue.add(audio)
                } else {
                    audio.stop()
                    Log.d(FindPuppyGame.TAG, "Stopped player! Length: ${audioQueue.size}")
                }

                delay(250)
            }
        }
    }

    fun playSpecific(resourceId: Int, speed: Float = 1f) {
        val audioPlayer = AudioPlayer(context)
        audioPlayer.play(resourceId, onPrepareCallback = {
            it.playbackParams = PlaybackParams()
                .setSpeed(speed)
        })
        audioQueue.add(audioPlayer)
    }

    private fun getAudioResourceFileBy(type: Tile.Type): List<Int> {
        return when (type) {
            Tile.Type.WithPuppy -> listOf(
                R.raw.bark_puppy_0,
                R.raw.bark_puppy_1,
                R.raw.bark_puppy_2,
            )

            Tile.Type.WithEnemyMan -> listOf(
                R.raw.scream_man_0,
                R.raw.scream_man_1,
                R.raw.scream_man_2,
            )

            Tile.Type.WithEnemyWoman -> listOf(
                R.raw.scream_woman_0,
                R.raw.scream_woman_1,
                R.raw.scream_woman_2,
                R.raw.scream_woman_3,
                R.raw.scream_woman_4,
                R.raw.scream_woman_5,
            )

            else ->
                throw NoSuchFieldException(
                    "unexpected tile type for requesting audio resource: ${type.name}"
                )
        }
    }


    fun playRandomAudio(type: Tile.Type) {
        val listOfSounds = getAudioResourceFileBy(type)
        val resourceId = listOfSounds[Random.nextInt(0, listOfSounds.size - 1)]

        val player = AudioPlayer(context)
        player.play(resourceId)

        audioQueue.add(player)

    }

    fun playUiSound() {
        playSpecific(R.raw.sound_button_pressed)
    }

}
package com.mrsep.musicrecognizer.data.player

import java.io.File
import kotlin.time.Duration

sealed class PlayerStatusDo {

    data object Idle : PlayerStatusDo()

    data class Started(
        val record: File,
        val duration: Duration
    ) : PlayerStatusDo()

    data class Paused(
        val record: File,
        val duration: Duration
    ) : PlayerStatusDo()

    data class Error(
        val record: File,
        val message: String
    ) : PlayerStatusDo()

}
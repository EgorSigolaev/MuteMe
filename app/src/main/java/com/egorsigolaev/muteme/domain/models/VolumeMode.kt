package com.egorsigolaev.muteme.domain.models

import com.egorsigolaev.muteme.R

enum class VolumeMode {
    VOLUME_MODE, VIBRATION_MODE, SILENT_MODE, NONE;

    fun stringRes(): Int {
        return when(this){
            VOLUME_MODE -> R.string.volume
            VIBRATION_MODE -> R.string.vibration
            SILENT_MODE -> R.string.silent
            NONE -> R.string.not_specified
        }
    }
}
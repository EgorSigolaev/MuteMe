package com.egorsigolaev.muteme.presentation.screens.placesettings.models

import com.egorsigolaev.muteme.domain.models.VolumeMode

sealed class PlaceSettingsViewEvent {
    data class SelectVolumeMode(val volumeMode: VolumeMode): PlaceSettingsViewEvent()
}
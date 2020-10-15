package com.egorsigolaev.muteme.presentation.screens.placesettings.models

import com.egorsigolaev.muteme.domain.models.VolumeMode
import com.google.android.libraries.places.api.model.Place

data class PlaceSettingsViewState(
    val volumeMode: VolumeMode?,
    val loadingState: PlaceSettingsLoadingState
)

sealed class PlaceSettingsLoadingState {
    object VolumeModeSelected: PlaceSettingsLoadingState()
    object VolumeModeNotSelected: PlaceSettingsLoadingState()
}
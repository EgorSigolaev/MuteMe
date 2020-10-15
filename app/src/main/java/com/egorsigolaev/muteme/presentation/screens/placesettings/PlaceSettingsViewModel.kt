package com.egorsigolaev.muteme.presentation.screens.placesettings

import androidx.lifecycle.ViewModel
import com.egorsigolaev.muteme.R
import com.egorsigolaev.muteme.domain.models.VolumeMode
import com.egorsigolaev.muteme.presentation.base.BaseViewModel
import com.egorsigolaev.muteme.presentation.screens.placesettings.models.PlaceSettingsLoadingState
import com.egorsigolaev.muteme.presentation.screens.placesettings.models.PlaceSettingsViewAction
import com.egorsigolaev.muteme.presentation.screens.placesettings.models.PlaceSettingsViewEvent
import com.egorsigolaev.muteme.presentation.screens.placesettings.models.PlaceSettingsViewState
import javax.inject.Inject

class PlaceSettingsViewModel @Inject constructor() : BaseViewModel<PlaceSettingsViewState,PlaceSettingsViewAction,PlaceSettingsViewEvent>() {

    init {
        viewState = PlaceSettingsViewState(volumeMode = VolumeMode.NONE, loadingState = PlaceSettingsLoadingState.VolumeModeNotSelected)
    }

    override fun obtainEvent(viewEvent: PlaceSettingsViewEvent) {
        when(viewEvent){
            is PlaceSettingsViewEvent.SelectVolumeMode -> {
                viewState = viewState.copy(volumeMode = viewEvent.volumeMode, loadingState = PlaceSettingsLoadingState.VolumeModeSelected)
            }
        }
    }

    fun fieldsFilledCorrect(placeName: String, placeDescription: String): Boolean{
        if(placeName.isEmpty()){
            viewAction = PlaceSettingsViewAction.ShowError(R.string.place_name_not_filled_error)
            return false
        }
        return true
    }

}
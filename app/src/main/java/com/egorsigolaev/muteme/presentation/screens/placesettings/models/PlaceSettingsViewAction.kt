package com.egorsigolaev.muteme.presentation.screens.placesettings.models

sealed class PlaceSettingsViewAction {
    data class ShowError(val message: Any): PlaceSettingsViewAction()
}
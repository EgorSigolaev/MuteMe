package com.egorsigolaev.muteme.presentation.screens.addplace

sealed class AddPlaceViewAction {
    data class ShowError(val message: Any): AddPlaceViewAction()
}
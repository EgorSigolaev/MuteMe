package com.egorsigolaev.muteme.presentation.screens.addplace.models

sealed class AddPlaceViewAction {
    data class ShowError(val message: Any): AddPlaceViewAction()
    object CollapseRecyclerView: AddPlaceViewAction()
}
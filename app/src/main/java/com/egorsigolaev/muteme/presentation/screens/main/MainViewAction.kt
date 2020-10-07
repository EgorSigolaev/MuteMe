package com.egorsigolaev.muteme.presentation.screens.main

import com.egorsigolaev.muteme.data.models.Place

sealed class MainViewAction {
    data class ShowError(val message: Any): MainViewAction()
    data class StartListenLocation(val places: List<Place>): MainViewAction()
}
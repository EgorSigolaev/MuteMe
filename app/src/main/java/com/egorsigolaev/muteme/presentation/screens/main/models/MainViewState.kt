package com.egorsigolaev.muteme.presentation.screens.main.models

import com.egorsigolaev.muteme.data.models.Place

sealed class MainViewState{
    object ScreenShowed: MainViewState()
    data class PlacesLoaded(val places: List<Place>? = null): MainViewState()
}
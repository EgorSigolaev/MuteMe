package com.egorsigolaev.muteme.presentation.screens.addplace

import com.egorsigolaev.muteme.data.models.Place
import com.egorsigolaev.muteme.presentation.screens.main.MainViewState

sealed class AddPlaceViewState{
    object ScreenShowed: AddPlaceViewState()
}
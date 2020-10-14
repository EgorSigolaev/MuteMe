package com.egorsigolaev.muteme.presentation.screens.addplace.models

import com.egorsigolaev.muteme.data.models.network.Result
import com.egorsigolaev.muteme.data.models.network.SearchPlace

sealed class AddPlaceViewState{
    object ScreenShowed: AddPlaceViewState()
    data class SearchPlaceLoaded(val places: List<SearchPlace>): AddPlaceViewState()
    data class SearchPlaceError(val errorCode: String): AddPlaceViewState()
    data class PlaceInfoLoaded(val result: Result): AddPlaceViewState()
    data class ScreenStateChanged(val screenState: ScreenState): AddPlaceViewState()
}

sealed class ScreenState{
    data class Loading(val message: Any): ScreenState()
    object Finished: ScreenState()
}
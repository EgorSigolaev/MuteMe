package com.egorsigolaev.muteme.presentation.screens.addplace.models

import android.view.MotionEvent
import android.view.View
import com.egorsigolaev.muteme.data.models.UserCoordinates

sealed class AddPlaceViewEvent{
    data class GetSearchPlaces(val key: String, val input: String, val language: String, val location: UserCoordinates?): AddPlaceViewEvent()
    data class GetPlaceInfo(val key: String, val placeId: String, val language: String): AddPlaceViewEvent()
}
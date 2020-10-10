package com.egorsigolaev.muteme.data

import com.egorsigolaev.muteme.data.models.UserCoordinates
import com.egorsigolaev.muteme.data.models.network.PlaceInfoResponse
import com.egorsigolaev.muteme.data.models.network.SearchPlaceResponse
import io.reactivex.Single

interface AddPlaceRepository {

    fun getSearchPlaces(key: String, input: String, language: String, location: UserCoordinates?): Single<SearchPlaceResponse>

    fun getPlaceInfo(key: String, placeId: String, language: String): Single<PlaceInfoResponse>

}
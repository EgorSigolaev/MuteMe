package com.egorsigolaev.muteme.data.impl

import com.egorsigolaev.muteme.data.AddPlaceRepository
import com.egorsigolaev.muteme.data.models.UserCoordinates
import com.egorsigolaev.muteme.data.models.network.PlaceInfoResponse
import com.egorsigolaev.muteme.data.models.network.SearchPlaceResponse
import com.egorsigolaev.muteme.data.source.remote.addplace.AddPlaceApi
import io.reactivex.Single
import javax.inject.Inject

class AddPlaceRepositoryImpl @Inject constructor(private val api: AddPlaceApi): AddPlaceRepository {
    override fun getSearchPlaces(key: String, input: String, language: String, location: UserCoordinates?): Single<SearchPlaceResponse> {
        return api.getSearchPlaces(key = key, input = input, language = language, location = getLocation(location))
    }

    override fun getPlaceInfo(key: String, placeId: String, language: String): Single<PlaceInfoResponse> {
        return api.getPlaceInfo(key = key, placeId = placeId, language = language)
    }

    private fun getLocation(location: UserCoordinates?): String{
        location?.let {
            return "${it.latitude},${it.longitude}"
        } ?: run {
            return ""
        }
    }
}
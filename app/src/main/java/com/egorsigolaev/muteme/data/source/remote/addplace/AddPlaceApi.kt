package com.egorsigolaev.muteme.data.source.remote.addplace

import com.egorsigolaev.muteme.data.models.network.PlaceInfoResponse
import com.egorsigolaev.muteme.data.models.network.SearchPlaceResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface AddPlaceApi {

    @GET("place/autocomplete/json")
    fun getSearchPlaces(
        @Query("key") key: String,
        @Query("input") input: String,
        @Query("language") language: String,
        @Query("location") location: String?,
        @Query("radius") radius: String = "50000",
        @Query("strictbounds") strictbounds: Boolean = true
    ): Single<SearchPlaceResponse>

    @GET("place/details/json")
    fun getPlaceInfo(
        @Query("key") key: String,
        @Query("place_id") placeId: String,
        @Query("language") language: String,
        @Query("fields") fields: String = "geometry"
    ): Single<PlaceInfoResponse>

}
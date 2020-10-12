package com.egorsigolaev.muteme.data.models.network

import com.google.gson.annotations.SerializedName

class PlaceInfoResponse (
    val result: Result,
    val status: String
)
data class Result(
    val geometry: Geometry
)

data class Geometry(
    val location: Location
)

data class Location(
    @SerializedName("lat")
    val latitude: Double,

    @SerializedName("lng")
    val longitude: Double
)
package com.egorsigolaev.muteme.data.models.network

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
    val lat: String,
    val lng: String
)
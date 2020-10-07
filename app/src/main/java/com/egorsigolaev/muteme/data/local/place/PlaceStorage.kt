package com.egorsigolaev.muteme.data.local.place

import com.egorsigolaev.muteme.data.models.Place
import io.reactivex.Completable
import io.reactivex.Single

interface PlaceStorage{
    fun getPlacesByLastVisitTime(): Single<List<Place>>
    fun getPlacesByFirstVisitTime(): Single<List<Place>>
    fun getAllPlaces(): Single<List<Place>>
    fun addPlace(place: Place): Completable
    fun removePlace(place: Place): Completable
    fun updatePlace(place: Place)
}
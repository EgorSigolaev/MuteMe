package com.egorsigolaev.muteme.data.impl

import com.egorsigolaev.muteme.data.local.place.PlaceStorage
import com.egorsigolaev.muteme.data.models.Place
import com.egorsigolaev.muteme.data.PlaceRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class PlaceRepositoryImpl @Inject constructor(private val placeStorage: PlaceStorage):
    PlaceRepository {
    override fun getPlacesByLastVisitTime(): Single<List<Place>> {
        return placeStorage.getPlacesByLastVisitTime()
    }

    override fun getPlacesByFirstVisitTime(): Single<List<Place>> {
        return placeStorage.getPlacesByFirstVisitTime()
    }

    override fun getAllPlaces(): Single<List<Place>> {
        return placeStorage.getAllPlaces()
    }

    override fun addPlace(place: Place): Completable {
        return placeStorage.addPlace(place = place)
    }

    override fun removePlace(place: Place): Completable {
        return placeStorage.removePlace(place = place)
    }

    override fun updatePlace(place: Place) {
        placeStorage.updatePlace(place = place)
    }

}
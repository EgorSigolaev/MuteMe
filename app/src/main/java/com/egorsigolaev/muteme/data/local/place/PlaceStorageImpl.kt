package com.egorsigolaev.muteme.data.local.place

import com.egorsigolaev.muteme.data.models.Place
import io.reactivex.Completable
import io.reactivex.Single

class PlaceStorageImpl(private val placeDao: PlaceDao): PlaceStorage{
    override fun getPlacesByLastVisitTime(): Single<List<Place>> {
        return placeDao.getPlacesByLastVisitTime()
    }

    override fun getPlacesByFirstVisitTime(): Single<List<Place>> {
        return placeDao.getPlacesByFirstVisitTime()
    }

    override fun getAllPlaces(): Single<List<Place>> {
        return placeDao.getAllPlaces()
    }

    override fun addPlace(place: Place): Completable {
        return placeDao.addPlace(place = place)
    }

    override fun removePlace(place: Place): Completable {
        return placeDao.removePlace(place = place)
    }

    override fun updatePlace(place: Place) {
        placeDao.updatePlace(place = place)
    }

}
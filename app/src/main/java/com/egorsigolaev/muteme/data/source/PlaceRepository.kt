package com.egorsigolaev.muteme.data.source

import androidx.room.*
import com.egorsigolaev.muteme.data.models.Place
import com.egorsigolaev.muteme.data.models.Place.Companion
import com.egorsigolaev.muteme.data.models.Place.Companion.PLACE_COORDINATES_FIELD
import com.egorsigolaev.muteme.data.models.Place.Companion.PLACE_VISIT_TIME_FIELD
import com.egorsigolaev.muteme.data.models.Place.Companion.TABLE_NAME
import io.reactivex.Completable
import io.reactivex.Single

interface PlaceRepository{

    fun getPlacesByLastVisitTime(): Single<List<Place>>
    fun getPlacesByFirstVisitTime(): Single<List<Place>>
    fun getAllPlaces(): Single<List<Place>>
    fun addPlace(place: Place): Completable
    fun removePlace(place: Place): Completable
    fun updatePlace(place: Place)
}
package com.egorsigolaev.muteme.data.local.place

import androidx.room.*
import com.egorsigolaev.muteme.data.models.Place
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface PlaceDao {
    @Query("SELECT * FROM ${Place.TABLE_NAME} ORDER BY ${Place.PLACE_VISIT_TIME_FIELD}")
    fun getPlacesByLastVisitTime(): Single<List<Place>>

    @Query("SELECT * FROM ${Place.TABLE_NAME} ORDER BY ${Place.PLACE_VISIT_TIME_FIELD} ASC")
    fun getPlacesByFirstVisitTime(): Single<List<Place>>

    @Query("SELECT * FROM ${Place.TABLE_NAME}")
    fun getAllPlaces(): Single<List<Place>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPlace(place: Place): Completable

    @Delete
    fun removePlace(place: Place): Completable

    @Update
    fun updatePlace(place: Place)
}
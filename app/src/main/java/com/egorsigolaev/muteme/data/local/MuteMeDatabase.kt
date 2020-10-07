package com.egorsigolaev.muteme.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.egorsigolaev.muteme.data.local.place.PlaceDao
import com.egorsigolaev.muteme.data.models.Place
import com.egorsigolaev.muteme.data.models.converters.UserCoordinatesConverter
import com.egorsigolaev.muteme.data.models.converters.VolumeModeConverter

@Database(
    entities = [
        Place::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(UserCoordinatesConverter::class, VolumeModeConverter::class)
abstract class MuteMeDatabase : RoomDatabase() {

    abstract fun placeDao(): PlaceDao

}
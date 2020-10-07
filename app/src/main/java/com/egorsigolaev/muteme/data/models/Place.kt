package com.egorsigolaev.muteme.data.models

import android.os.Parcelable
import androidx.room.*
import com.egorsigolaev.muteme.data.models.Place.Companion.TABLE_NAME
import com.egorsigolaev.muteme.data.models.converters.UserCoordinatesConverter
import com.egorsigolaev.muteme.data.models.converters.VolumeModeConverter
import kotlinx.android.parcel.Parcelize
import kotlin.math.*

@Parcelize
@Entity(tableName = TABLE_NAME)
data class Place(

    @ColumnInfo(name = PLACE_NAME_FIELD)
    val name: String,

    @ColumnInfo(name = PLACE_COORDINATES_FIELD)
    @TypeConverters(UserCoordinatesConverter::class)
    val coordinates: UserCoordinates,

    @ColumnInfo(name = PLACE_VISIT_TIME_FIELD)
    val visitTime: Long,

    @ColumnInfo(name = PLACE_CREATE_TIME_FIELD)
    val createTime: Long = System.currentTimeMillis(),

    @ColumnInfo(name = PLACE_VOLUME_MODE_FIELD)
    @TypeConverters(VolumeModeConverter::class)
    val needVolumeMode: VolumeMode,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = PLACE_ID_FIELD)
    val id: Int? = null
): Parcelable{

    // Дистанция по прямой
    fun distanceBetweenMeters(currentCoordinates: UserCoordinates): Int{
        val lon1 = Math.toRadians(currentCoordinates.longitude)
        val lon2 = Math.toRadians(coordinates.longitude)
        val lat1 = Math.toRadians(currentCoordinates.latitude)
        val lat2 = Math.toRadians(coordinates.latitude)

        val dlon = lon2 - lon1
        val dlat = lat2 - lat1
        val a = (sin(dlat / 2).pow(2.0) + (cos(lat1) * cos(lat2) * sin(dlon / 2).pow(2.0)))
        val c = 2 * asin(sqrt(a))
        val earthRadius = 6371
        return (c * earthRadius * 1000).toInt()
    }

    companion object{
        const val TABLE_NAME = "places"

        const val PLACE_ID_FIELD = "place_id"
        const val PLACE_NAME_FIELD = "place_name"
        const val PLACE_COORDINATES_FIELD = "place_coordinates"
        const val PLACE_VISIT_TIME_FIELD = "place_visit_time"
        const val PLACE_CREATE_TIME_FIELD = "place_create_time_field"
        const val PLACE_VOLUME_MODE_FIELD = "place_volume_mode_field"
    }
}
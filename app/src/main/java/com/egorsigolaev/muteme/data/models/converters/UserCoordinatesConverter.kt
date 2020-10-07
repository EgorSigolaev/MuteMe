package com.egorsigolaev.muteme.data.models.converters

import androidx.room.TypeConverter
import com.egorsigolaev.muteme.data.models.UserCoordinates
import com.google.gson.Gson

class UserCoordinatesConverter {

    @TypeConverter
    fun fromCoordinates(coordinates: UserCoordinates?): String?{
        return if(coordinates == null){
            null
        }else{
            Gson().toJson(coordinates)
        }

    }

    @TypeConverter
    fun toCoordinates(data: String?): UserCoordinates?{
        return if(data == null){
            null
        }else{
            Gson().fromJson(data, UserCoordinates::class.java)
        }
    }

}
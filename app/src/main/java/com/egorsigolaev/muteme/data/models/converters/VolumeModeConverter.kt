package com.egorsigolaev.muteme.data.models.converters

import androidx.room.TypeConverter
import com.egorsigolaev.muteme.domain.models.VolumeMode
import com.google.gson.Gson

class VolumeModeConverter {

    @TypeConverter
    fun fromVolumeMode(volumeMode: VolumeMode): String{
        return Gson().toJson(volumeMode)
    }

    @TypeConverter
    fun toVolumeMode(data: String): VolumeMode {
        return Gson().fromJson(data, VolumeMode::class.java)
    }

}
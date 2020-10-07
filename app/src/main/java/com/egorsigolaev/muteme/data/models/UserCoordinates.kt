package com.egorsigolaev.muteme.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserCoordinates(
    val latitude: Double,
    val longitude: Double
): Parcelable
package com.egorsigolaev.muteme.domain.models

import android.os.Parcelable
import com.egorsigolaev.muteme.data.models.Place
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MainScreenData(
    val nearestPlace: Place,
    val distanceBetweenMeters: Int,
    val timeLeftMinutes: Int
): Parcelable
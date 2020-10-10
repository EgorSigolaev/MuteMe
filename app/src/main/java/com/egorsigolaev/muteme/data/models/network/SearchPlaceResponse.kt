package com.egorsigolaev.muteme.data.models.network

import com.google.gson.annotations.SerializedName

data class SearchPlaceResponse(
    val status: String,
    val predictions: List<SearchPlace>
)

data class SearchPlace(
    @SerializedName("place_id")
    val placeId: String,

    val description: String,

    @SerializedName("structured_formatting")
    val formatted_info: FormattedInfo
)

data class FormattedInfo(
    @SerializedName("main_text")
    val mainText: String,

    @SerializedName("secondary_text")
    val secondaryText: String
)
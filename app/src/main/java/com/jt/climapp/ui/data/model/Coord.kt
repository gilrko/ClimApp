package com.jt.climapp.ui.data.model

import com.google.gson.annotations.SerializedName

data class Coord(
    @SerializedName("lon")
    val longuitude: Double,
    @SerializedName("lat")
    val latitude: Double,
)

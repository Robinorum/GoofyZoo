package fr.isen.goofyzoo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomGeoPoint(
    val lat: Double = 0.0,
    val lon: Double = 0.0
) : Parcelable {
    fun toOsmGeoPoint(): org.osmdroid.util.GeoPoint = org.osmdroid.util.GeoPoint(lat, lon)
}
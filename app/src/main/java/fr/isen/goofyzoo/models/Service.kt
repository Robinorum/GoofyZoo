package fr.isen.goofyzoo.models

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Service(
    val id: String = "",
    val name: String = "",
    val id_service: String = "",
    @get:PropertyName("geopoint") @set:PropertyName("geopoint")
    var geopoint: CustomGeoPoint? = null
) : Parcelable
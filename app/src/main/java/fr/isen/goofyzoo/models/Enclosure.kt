package fr.isen.goofyzoo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.firebase.database.PropertyName

@Parcelize
data class Enclosure(
    val id: String = "",
    val id_biomes: String = "",
    val meal: String = "",
    val animals: List<Animal> = emptyList(),
    @get:PropertyName("is_open") @set:PropertyName("is_open")
    var is_open: Boolean = false
) : Parcelable
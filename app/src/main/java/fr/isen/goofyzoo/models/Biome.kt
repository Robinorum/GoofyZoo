package fr.isen.goofyzoo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Biome(
    val id: String = "",
    val color: String = "",
    val name: String = "",
    val enclosures: List<Enclosure> = emptyList(),
    val services: List<Service> = emptyList()
) : Parcelable

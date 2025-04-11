package fr.isen.goofyzoo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review(
    val id: Int = 0,
    val enclosureId: String = "",
    val userId: String = "",
    val username: String = "",
    val rating: Int = 0,
    val comment: String = "",

) : Parcelable

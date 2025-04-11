package fr.isen.goofyzoo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val admin: Boolean = false,
    val employee: Boolean = false,
    val reviews: List<Review> = emptyList()
) : Parcelable

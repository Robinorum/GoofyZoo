package fr.isen.goofyzoo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Animal(
    val id: String = "",
    val name: String = "",
    val id_enclos: String = "",
    val id_animal: String = ""
) : Parcelable

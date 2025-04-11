package fr.isen.goofyzoo.models

import java.io.Serializable

data class User(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val admin: Boolean = false,
    val employee: Boolean= false
) : Serializable
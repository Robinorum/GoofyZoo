package fr.isen.goofyzoo.models

data class Review(
    val id: Int = 0,
    val enclosureId: String = "",
    val userId: Int = 0,
    val rating: Int = 0,
    val comment: String = ""
)
package fr.isen.goofyzoo.models

data class Review(
    val id: Int,
    val enclosureId: Int,
    val userId: Int,
    val rating: Float,
    val comment: String
)

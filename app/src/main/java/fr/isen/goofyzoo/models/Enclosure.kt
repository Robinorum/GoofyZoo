package fr.isen.goofyzoo.models

data class Enclosure(
    val id: String,
    val idBiomes: String,
    val meal: String,
    val animals: List<Animal>
)
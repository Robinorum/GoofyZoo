package fr.isen.goofyzoo.screens.enclosures

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import fr.isen.goofyzoo.models.Enclosure
import fr.isen.goofyzoo.models.Review



@Composable
fun EnclosureDetailScreen(navController: NavHostController, userId: String, username: String) {
    val enclosure = navController.previousBackStackEntry?.savedStateHandle?.get<Enclosure>("enclosure")
    var rating by remember { mutableStateOf(0) }
    var reviewText by remember { mutableStateOf("") }
    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }
    val database = FirebaseDatabase.getInstance().reference

    var isEditing by remember { mutableStateOf(false) }
    var reviewBeingEdited by remember { mutableStateOf<Review?>(null) }


    // Vérifier si l'utilisateur a déjà posté un avis
    val hasUserReviewed by remember(reviews, userId) {
        derivedStateOf {
            reviews.any { it.userId == userId }
        }
    }

    LaunchedEffect(enclosure?.id) {
        enclosure?.let { enc ->
            database.child("zoo").get().addOnSuccessListener { snapshot ->
                for (biomeSnapshot in snapshot.children) {
                    val biomeId = biomeSnapshot.child("id").getValue(String::class.java)
                    if (biomeId == enc.id_biomes) {
                        for (enclosureSnapshot in biomeSnapshot.child("enclosures").children) {
                            val enclosureId = enclosureSnapshot.child("id").getValue(String::class.java)
                            if (enclosureId == enc.id) {
                                val fetchedReviews = enclosureSnapshot.child("reviews")
                                    .children
                                    .mapNotNull { it.getValue<Review>() }
                                reviews = fetchedReviews
                            }
                        }
                    }
                }
            }.addOnFailureListener { error ->
                println("Erreur lors de la récupération: ${error.message}")
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        enclosure?.let {
            Text(
                text = "Enclos n°${it.id}",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 26.sp),
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )

            // Affichage du statut de l'enclos
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = if (it.is_open) Color(0xFF009000) else Color(0xFFFF0000))
            ) {
                Text(
                    text = "Statut: ${if (it.is_open) "Ouvert" else "Fermé"}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Affichage des animaux dans l'enclos
            it.animals.forEach { animal ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F1))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = animal.name,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section "Votre avis"
            Text(
                text = "Votre avis:",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Affichage du champ d'avis uniquement si l'utilisateur n'a pas encore posté
            if (!hasUserReviewed) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 1..5) {
                        IconButton(onClick = { rating = i }) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "$i étoiles",
                                tint = if (i <= rating) Color.Yellow else Color.Gray
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text("Votre commentaire") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Button(
                    onClick = {
                        if (rating > 0 && reviewText.isNotBlank()) {
                            val newReview = Review(
                                id = reviews.size + 1,
                                enclosureId = it.id,
                                userId = userId,
                                username = username,
                                rating = rating,
                                comment = reviewText
                            )

                            database.child("users").child(userId).child("reviews").push().setValue(newReview)

                            database.child("zoo").get().addOnSuccessListener { snapshot ->
                                for (biomeSnapshot in snapshot.children) {
                                    val biomeId = biomeSnapshot.child("id").getValue(String::class.java)
                                    if (biomeId == it.id_biomes) {
                                        for (enclosureSnapshot in biomeSnapshot.child("enclosures").children) {
                                            val enclosureId = enclosureSnapshot.child("id").getValue(String::class.java)
                                            if (enclosureId == it.id) {
                                                enclosureSnapshot.ref.child("reviews").push().setValue(newReview)
                                                return@addOnSuccessListener
                                            }
                                        }
                                    }
                                }
                            }

                            reviews = reviews + newReview
                            rating = 0
                            reviewText = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Envoyer")
                }
            } else {
                val userReview = reviews.firstOrNull { it.userId == userId }
                if (userReview != null) {
                    // Affichage de l'avis de l'utilisateur dans un encadré
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                for (i in 1..5) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = "$i étoiles",
                                        tint = if (i <= userReview.rating) Color.Yellow else Color.Gray
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = userReview.comment,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Button(
                                    onClick = {
                                        // Logique pour supprimer l'avis
                                        userReview?.let { review ->
                                            // Récupérer la référence de la base de données
                                            database.child("zoo").get().addOnSuccessListener { snapshot ->
                                                for (biomeSnapshot in snapshot.children) {
                                                    val biomeId = biomeSnapshot.child("id").getValue(String::class.java)
                                                    if (biomeId == enclosure?.id_biomes) {
                                                        for (enclosureSnapshot in biomeSnapshot.child("enclosures").children) {
                                                            val enclosureId = enclosureSnapshot.child("id").getValue(String::class.java)
                                                            if (enclosureId == enclosure?.id) {
                                                                // Trouver l'avis à supprimer dans l'enclos
                                                                val reviewRef = enclosureSnapshot.child("reviews").children.firstOrNull {
                                                                    it.getValue<Review>()?.id == review.id
                                                                }?.ref
                                                                // Supprimer l'avis de l'enclos
                                                                reviewRef?.removeValue()
                                                            }
                                                        }
                                                    }
                                                }
                                            }.addOnFailureListener { error ->
                                                println("Erreur lors de la suppression dans l'enclos: ${error.message}")
                                            }

                                            // Supprimer l'avis de la collection de l'utilisateur
                                            database.child("users").child(userId).child("reviews").get().addOnSuccessListener { userSnapshot ->
                                                userSnapshot.children.firstOrNull {
                                                    it.getValue<Review>()?.id == review.id
                                                }?.ref?.removeValue()?.addOnSuccessListener {
                                                    // Mettre à jour la liste locale des avis
                                                    reviews = reviews.filter { it.id != review.id }
                                                }
                                            }.addOnFailureListener { error ->
                                                println("Erreur lors de la suppression dans la collection utilisateur: ${error.message}")
                                            }
                                        }
                                    },
                                    modifier = Modifier.width(100.dp)
                                ) {
                                    Text("Supprimer")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section "Avis des visiteurs"
            Text(
                text = "Avis des visiteurs:",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Affichage des avis des autres utilisateurs
            reviews.filter { it.userId != userId }.forEach { review ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Utilisateur: ${review.username}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 1..5) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "$i étoiles",
                                    tint = if (i <= review.rating) Color.Yellow else Color.Gray
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = review.comment,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                        )
                    }
                }
            }

        } ?: Text(
            text = "Chargement...",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
            modifier = Modifier.padding(16.dp)
        )
    }
}

package fr.isen.goofyzoo.screens.enclosures

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import fr.isen.goofyzoo.models.Enclosure
import fr.isen.goofyzoo.models.Review
import fr.isen.goofyzoo.R

@Composable
fun EnclosureDetailScreen(navController: NavHostController) {
    val enclosure = navController.previousBackStackEntry?.savedStateHandle?.get<Enclosure>("enclosure")
    var rating by remember { mutableStateOf(0) }
    var reviewText by remember { mutableStateOf("") }
    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }
    val database = FirebaseDatabase.getInstance().reference

    LaunchedEffect(enclosure?.id) {
        enclosure?.let { enc ->
            database.child("zoo").get().addOnSuccessListener { snapshot ->
                for (biomeSnapshot in snapshot.children) {
                    val biomeId = biomeSnapshot.child("id").getValue(String::class.java)
                    if (biomeId == enc.id_biomes.toString()) {
                        for (enclosureSnapshot in biomeSnapshot.child("enclosures").children) {
                            val enclosureId = enclosureSnapshot.child("id").getValue(String::class.java)
                            if (enclosureId == enc.id.toString()) {

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

            Text(
                text = "Repas prévu: ${it.meal}",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = "Votre avis:",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                for (i in 1..5) {
                    IconButton(onClick = { rating = i }) {
                        Icon(
                            painter = painterResource(id = R.drawable.star),
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
                            userId = 1,
                            rating = rating,
                            comment = reviewText
                        )
                        //database.child("zoo").child(it.id_biomes.toString()).child(it.id).child("reviews").push().setValue(newReview)


                        database.child("zoo").get().addOnSuccessListener { snapshot ->
                            for (biomeSnapshot in snapshot.children) {
                                val biomeId = biomeSnapshot.child("id").getValue(String::class.java)
                                if (biomeId == it.id_biomes.toString()) {
                                    for (enclosureSnapshot in biomeSnapshot.child("enclosures").children) {
                                        val enclosureId = enclosureSnapshot.child("id").getValue(String::class.java)
                                        if (enclosureId == it.id.toString()) {

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

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Avis des visiteurs:",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            reviews.forEach { review ->
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 1..5) {
                                Icon(
                                    painter = painterResource(id = R.drawable.star),
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
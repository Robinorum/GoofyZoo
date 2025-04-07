package fr.isen.goofyzoo.screens.profil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import fr.isen.goofyzoo.R
import fr.isen.goofyzoo.models.Review

@Composable
fun ProfileScreen(navController: NavHostController, UserId: String, Username: String) {
    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }
    val database = FirebaseDatabase.getInstance().reference

    // Récupérer les avis de l'utilisateur
    LaunchedEffect(UserId) {
        database.child("users").child(UserId).child("reviews").get().addOnSuccessListener { snapshot ->
            reviews = snapshot.children.mapNotNull { it.getValue<Review>() }
        }.addOnFailureListener { error ->
            println("Erreur lors de la récupération des avis : ${error.message}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Titre de la page
        Text(
            text = "Profil de $Username",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            color = colorResource(id = R.color.LightBrown),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Affichage des avis de l'utilisateur
        if (reviews.isNotEmpty()) {
            Text(
                text = "Vos avis",
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
                    colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.LightBrown))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Enclos n°${review.enclosureId}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Affichage des étoiles
                            for (i in 1..5) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "$i étoiles",
                                    tint = if (i <= review.rating) Color.Yellow else Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = review.comment,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                        )
                    }
                }
            }
        } else {
            Text(
                text = "Aucun avis trouvé.",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Boutons pour modifier le profil et déconnexion
        Spacer(modifier = Modifier.weight(1f)) // Push buttons to bottom
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Bouton de modification de profil
            Button(
                onClick = { /* TODO: Action pour modifier le profil */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.Brown))
            ) {
                Text("Modifier le profil")
            }

            // Bouton de déconnexion
            Button(
                onClick = { /* TODO: Action pour se déconnecter */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.Saffron))
            ) {
                Text("Se déconnecter")
            }
        }
    }
}

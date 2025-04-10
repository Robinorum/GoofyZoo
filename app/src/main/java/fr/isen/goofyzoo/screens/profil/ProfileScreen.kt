package fr.isen.goofyzoo.screens.profil

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import fr.isen.goofyzoo.AuthActivity
import fr.isen.goofyzoo.R
import fr.isen.goofyzoo.models.Review

@Composable
fun ProfileScreen(navController: NavHostController, UserId: String) {
    var usernameState by remember { mutableStateOf("") }
    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }
    var showEditField by remember { mutableStateOf(false) }
    var newUsername by remember { mutableStateOf("") }

    val database = FirebaseDatabase.getInstance().reference
    val context = LocalContext.current

    LaunchedEffect(UserId) {
        // Récupérer le nom d'utilisateur
        database.child("users").child(UserId).child("username").get().addOnSuccessListener { snapshot ->
            snapshot.getValue(String::class.java)?.let {
                usernameState = it
            }
        }

        // Récupérer les avis
        database.child("users").child(UserId).child("reviews").get().addOnSuccessListener { snapshot ->
            reviews = snapshot.children.mapNotNull { it.getValue<Review>() }
        }.addOnFailureListener { error ->
            println("Erreur lors de la récupération des avis : ${error.message}")
        }
    }

    // Ajout de la scrollabilité
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()), // Rendre la colonne scrollable
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Profil de $usernameState",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            color = colorResource(id = R.color.LightBrown),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Affichage des avis
        if (reviews.isNotEmpty()) {
            Text(
                text = stringResource(R.string.profile_champ1),
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                modifier = Modifier.padding(vertical = 8.dp)
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
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Enclos n°${review.enclosureId}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) { i ->
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "${i + 1} étoiles",
                                    tint = if (i < review.rating) Color.Yellow else Color.Gray
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
                text = stringResource(R.string.profile_no_comment),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    showEditField = !showEditField
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.Brown))
            ) {
                Text(if (showEditField) stringResource(R.string.profile_annuler) else stringResource(R.string.profile_button1))
            }

            if (showEditField) {
                OutlinedTextField(
                    value = newUsername,
                    onValueChange = { newUsername = it },
                    label = { Text(stringResource(R.string.profile_placeholder)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                Button(
                    onClick = {
                        if (newUsername.isNotBlank()) {
                            database.child("users").child(UserId).child("username").setValue(newUsername)
                                .addOnSuccessListener {
                                    usernameState = newUsername
                                    showEditField = false
                                }
                                .addOnFailureListener {
                                    println("Erreur lors de la mise à jour du nom d'utilisateur : ${it.message}")
                                }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.Brown))
                ) {
                    Text(stringResource(R.string.profile_valider))
                }
            }

            Button(
                onClick = {
                    val intent = Intent(context, AuthActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.Saffron))
            ) {
                Text(stringResource(R.string.profile_button2))
            }
        }
    }
}

package fr.isen.goofyzoo.screens.enclosures

import android.content.Context
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import fr.isen.goofyzoo.R
import fr.isen.goofyzoo.models.Enclosure
import fr.isen.goofyzoo.models.Review
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun EnclosureDetailScreen(navController: NavHostController, userId: String, username: String) {
    val enclosure = navController.previousBackStackEntry?.savedStateHandle?.get<Enclosure>("enclosure")
    var rating by remember { mutableStateOf(0) }
    var reviewText by remember { mutableStateOf("") }
    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }
    val database = FirebaseDatabase.getInstance().reference
    var errorMessage by remember { mutableStateOf("") }

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

            enclosure.geopoint?.let { geoPoint ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
                            val mapView = MapView(ctx).apply {
                                setTileSource(TileSourceFactory.MAPNIK)
                                setMultiTouchControls(true)
                                isHorizontalMapRepetitionEnabled = false
                                isVerticalMapRepetitionEnabled = false
                                setScrollableAreaLimitLatitude(43.63380, 43.61380, 0)
                                setScrollableAreaLimitLongitude(5.19964, 5.21964, 0)
                                controller.setZoom(17.0)
                                controller.setCenter(geoPoint.toOsmGeoPoint())
                                isClickable = false
                                zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                            }

                            val marker = Marker(mapView).apply {
                                position = geoPoint.toOsmGeoPoint()
                                title = "Enclos n°${enclosure.id}"
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            }
                            mapView.overlays.add(marker)

                            mapView
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.encloD_champ1),
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )

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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        label = { Text(stringResource(R.string.encloD_placeholder)) },
                        modifier = Modifier
                            .weight(1f) // Raccourcit le champ pour laisser de l'espace
                            .padding(end = 8.dp) // Espacement entre le champ et l'icône
                    )
                    IconButton(
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
                            } else {
                                errorMessage = "Veuillez sélectionner au moins une étoile et mettre un commentaire."
                            }
                        },
                        modifier = Modifier.size(48.dp) // Taille de la zone cliquable
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.send),
                            contentDescription = stringResource(R.string.encloD_button),

                        )
                    }
                }

                if (errorMessage.isNotBlank()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            } else {
                val userReview = reviews.firstOrNull { it.userId == userId }
                if (userReview != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
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
                            }

                            IconButton(
                                onClick = {
                                    userReview.let { review ->
                                        database.child("zoo").get().addOnSuccessListener { snapshot ->
                                            for (biomeSnapshot in snapshot.children) {
                                                val biomeId = biomeSnapshot.child("id").getValue(String::class.java)
                                                if (biomeId == enclosure.id_biomes) {
                                                    for (enclosureSnapshot in biomeSnapshot.child("enclosures").children) {
                                                        val enclosureId = enclosureSnapshot.child("id").getValue(String::class.java)
                                                        if (enclosureId == enclosure.id) {
                                                            val reviewRef = enclosureSnapshot.child("reviews").children.firstOrNull {
                                                                it.getValue<Review>()?.id == review.id
                                                            }?.ref
                                                            reviewRef?.removeValue()
                                                        }
                                                    }
                                                }
                                            }
                                        }.addOnFailureListener { error ->
                                            println("Erreur lors de la suppression dans l'enclos: ${error.message}")
                                        }

                                        database.child("users").child(userId).child("reviews").get().addOnSuccessListener { userSnapshot ->
                                            userSnapshot.children.firstOrNull {
                                                it.getValue<Review>()?.id == review.id
                                            }?.ref?.removeValue()?.addOnSuccessListener {
                                                reviews = reviews.filter { it.id != review.id }
                                            }
                                        }.addOnFailureListener { error ->
                                            println("Erreur lors de la suppression dans la collection utilisateur: ${error.message}")
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.bin),
                                    contentDescription = stringResource(R.string.encloD_button2),
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.encloD_champ2),
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            reviews.filter { it.userId != userId }.forEach { review ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Utilisateur: ${review.username}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
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
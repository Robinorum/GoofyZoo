package fr.isen.goofyzoo.screens.enclosures

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.database.*
import fr.isen.goofyzoo.R
import fr.isen.goofyzoo.models.Biome


@Composable
fun EnclosuresListScreen(navController: NavHostController) {
    val database = FirebaseDatabase.getInstance().getReference("zoo")
    var biomes by remember { mutableStateOf(emptyList<Biome>()) }

    LaunchedEffect(Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val biomeList = snapshot.children.mapNotNull { it.getValue(Biome::class.java) }
                biomes = biomeList
            }

            override fun onCancelled(error: DatabaseError) {
                println("Erreur Firebase: ${error.message}")
            }
        })
    }

    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        if (biomes.isEmpty()) {
            item {
                Text(
                    text = "Chargement...",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            item {
                Text(
                    text = stringResource(R.string.encloL_Titre),
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 26.sp),
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }

            items(biomes) { biome ->
                Column(
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                ) {
                    Text(
                        text = biome.name,
                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 24.sp),
                        color = Color.White,
                        modifier = Modifier
                            .background(Color(android.graphics.Color.parseColor(biome.color)), shape = RoundedCornerShape(12.dp))
                            .padding(16.dp)
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )

                    Text(
                        text = stringResource(R.string.encloL_SousTitre1),
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )

                    if (biome.enclosures.isEmpty()) {
                        Text(
                            text = "Aucun enclos",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    } else {
                        biome.enclosures.forEach { enclosure ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.currentBackStackEntry?.savedStateHandle?.set("enclosure", enclosure)
                                        navController.navigate("enclosure_detail")
                                    }
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFEFEFEF),
                                border = BorderStroke(1.dp, Color.Gray)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Enclos n°${enclosure.id}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = if (enclosure.is_open) "Ouvert" else "Fermé",
                                        style = MaterialTheme.typography.bodySmall.copy(color = if (enclosure.is_open) Color.Green else Color.Red)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



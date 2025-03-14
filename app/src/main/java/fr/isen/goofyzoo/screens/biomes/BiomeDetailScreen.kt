package fr.isen.goofyzoo.screens.biomes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import fr.isen.goofyzoo.models.Biome

@Composable
fun BiomeDetailScreen(navController: NavHostController) {
    val biome = navController.previousBackStackEntry?.savedStateHandle?.get<Biome>("biome")

    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        item {
            biome?.let {
                Text(
                    text = "Biome: ${it.name}",
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 24.sp),
                    color = Color.White,
                    modifier = Modifier
                        .background(Color(android.graphics.Color.parseColor(it.color)), shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                        .padding(bottom = 16.dp)
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Enclos:",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                it.enclosures.forEach { enclosure ->
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

                Text(
                    text = "Services:",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                it.services.forEach { service ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Text(
                            text = service.name,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } ?: Text(
                text = "Chargement...",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}






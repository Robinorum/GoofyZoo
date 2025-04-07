package fr.isen.goofyzoo.screens.services

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.goofyzoo.models.Biome

@Composable
fun ServicesListScreen(navController: NavHostController) {
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
                    text = "Liste des services",
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
                        text = "Services:",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )

                    if (biome.services.isEmpty()) {
                        Text(
                            text = "Aucun service disponible",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    } else {
                        biome.services.forEach { service ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.currentBackStackEntry?.savedStateHandle?.set("service", service)
                                        navController.navigate("service_detail")
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
                                        text = service.name,
                                        style = MaterialTheme.typography.bodyMedium
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

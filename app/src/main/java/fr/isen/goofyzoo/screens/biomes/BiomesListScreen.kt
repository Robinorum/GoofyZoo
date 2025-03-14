package fr.isen.goofyzoo.screens.biomes

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.navigation.NavHostController
import com.google.firebase.database.*
import fr.isen.goofyzoo.models.Biome

//@Composable
//fun BiomeListScreen(navController: NavHostController) {
//    var biomes by remember { mutableStateOf<List<Biome>>(emptyList()) }
//    val database = FirebaseDatabase.getInstance().reference.child("zoo")
//    val coroutineScope = rememberCoroutineScope()
//
//    LaunchedEffect(Unit) {
//        database.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val biomeList = snapshot.children.mapNotNull { it.getValue(Biome::class.java) }
//                biomes = biomeList
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                println("Erreur Firebase: ${error.message}")
//            }
//        })
//    }
//
//
//    LazyColumn(
//        modifier = Modifier
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        items(biomes) { biome ->
//            BiomeCard(biome, navController)
//        }
//    }
//}
//
//@Composable
//fun BiomeCard(biome: Biome, navController: NavHostController) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(text = "🌿 ${biome.name}", style = MaterialTheme.typography.headlineSmall)
//            Text(text = "Couleur: ${biome.color}", style = MaterialTheme.typography.bodyMedium)
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Affichage des enclos
//            Text(text = "🦁 Enclos:", style = MaterialTheme.typography.bodyLarge)
//            biome.enclosures.forEach { enclosure ->
//                Text(text = "- Enclos ${enclosure.id}: ${if (enclosure.is_open) "✅ Ouvert" else "❌ Fermé"}", style = MaterialTheme.typography.bodyMedium)
//
//                // Affichage des animaux
//                enclosure.animals.forEach { animal ->
//                    Text(text = "    🐾 ${animal.name}", style = MaterialTheme.typography.bodySmall)
//                }
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Affichage des services
//            Text(text = "🏠 Services:", style = MaterialTheme.typography.bodyLarge)
//            biome.services.forEach { service ->
//                Text(text = "- ${service.name}", style = MaterialTheme.typography.bodyMedium)
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Bouton pour voir plus de détails (ex: ouvrir un écran détail)
//            Button(onClick = { navController.navigate("biome_detail/${biome.id}") }) {
//                Text("Voir détails")
//            }
//        }
//    }
//}

@Composable
fun BiomeListScreen(navController: NavHostController) {
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

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Text(
                text = "Liste des biomes",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 26.sp),
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }

        items(biomes) { biome ->
            BiomeItem(biome, navController)
        }
    }
}

@Composable
fun BiomeItem(biome: Biome, navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(android.graphics.Color.parseColor(biome.color)), shape = RoundedCornerShape(12.dp))
            .clickable {
                navController.currentBackStackEntry?.savedStateHandle?.set("biome", biome)
                navController.navigate("biome_detail")
            }
            .padding(16.dp)
    ) {
        Text(text = biome.name, color = Color.White)
    }
}


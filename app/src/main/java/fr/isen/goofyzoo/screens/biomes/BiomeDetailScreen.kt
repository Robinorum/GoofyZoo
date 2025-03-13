package fr.isen.goofyzoo.screens.biomes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun BiomeDetailScreen(navController: NavHostController, biomeId: String?) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Détails du Biome: $biomeId")

        Button(onClick = { navController.navigate("enclosure_detail/10") }) {
            Text("Voir l'Enclos 10")
        }
    }
}


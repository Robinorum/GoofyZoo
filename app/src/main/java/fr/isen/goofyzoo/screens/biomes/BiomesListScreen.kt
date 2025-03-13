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
fun BiomeListScreen(navController: NavHostController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Liste des Biomes")

        Button(onClick = { navController.navigate("biome_detail/1") }) {
            Text("Voir le Biome 1")
        }
    }
}

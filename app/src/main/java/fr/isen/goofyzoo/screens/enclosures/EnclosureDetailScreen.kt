package fr.isen.goofyzoo.screens.enclosures

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun EnclosureDetailScreen(navController: NavHostController, enclosureId: String?) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Détails de l'Enclos: $enclosureId")

        Button(onClick = { navController.navigate("animal_detail/1") }) {
            Text("Voir l'Animal 1")
        }
    }
}

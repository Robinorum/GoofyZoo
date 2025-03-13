package fr.isen.goofyzoo.screens.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AdminDashboardScreen(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Tableau de bord de l'admin")
        Button(onClick = { navController.navigate("maintenance_screen") }) {
            Text("Gestion de la maintenance")
        }
        Button(onClick = { navController.navigate("feeding_schedule_screen") }) {
            Text("Gestion des horaires de nourrissage")
        }
    }
}

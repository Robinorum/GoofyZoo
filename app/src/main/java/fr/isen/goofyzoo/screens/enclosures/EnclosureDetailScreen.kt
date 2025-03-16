package fr.isen.goofyzoo.screens.enclosures

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import fr.isen.goofyzoo.models.Enclosure

@Composable
fun EnclosureDetailScreen(navController: NavHostController) {
    val enclosure = navController.previousBackStackEntry?.savedStateHandle?.get<Enclosure>("enclosure")

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
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

            Text(
                text = "Repas prévu: ${it.meal}",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = "Animaux de l'enclos:",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            it.animals.forEach { animal ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F1))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = animal.name,
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

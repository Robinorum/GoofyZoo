package fr.isen.goofyzoo.screens.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.isen.goofyzoo.R

@Composable
fun AdminDashboardScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Affichage du logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo du Zoo",
            modifier = Modifier.size(120.dp)
        )

        // Titre
        Text(
            text = "Tableau de Bord Admin",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Boutons stylisés
        Button(
            onClick = { navController.navigate("maintenance_screen") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Icon(painter = painterResource(id = R.drawable.hammer), contentDescription = "Maintenance", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Gestion de la Maintenance", color = Color.White)
        }

        Button(
            onClick = { navController.navigate("feeding_schedule_screen") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
        ) {
            Icon(painter = painterResource(id = R.drawable.burger), contentDescription = "Nourrissage", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Gestion des Horaires de Nourrissage", color = Color.White)
        }
    }
}

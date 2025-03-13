package fr.isen.goofyzoo.screens.enclosures

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AnimalDetailScreen(animalId: String?) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Détails de l'Animal: $animalId")
    }
}


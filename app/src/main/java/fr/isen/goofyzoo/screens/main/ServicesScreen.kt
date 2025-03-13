package fr.isen.goofyzoo.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ServicesScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Liste des services du zoo")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* Action pour aller vers un détail */ }) {
            Text("Voir les détails")
        }
    }
}

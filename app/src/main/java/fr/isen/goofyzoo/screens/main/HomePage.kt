package fr.isen.goofyzoo.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import fr.isen.goofyzoo.R

@Composable
fun HomePage(navController: NavHostController,) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Bouton Profil en haut à droite
        Button(
            onClick = {navController.navigate("profile_screen") },
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.LightBrown)),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = "Mon Profil",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
        }

        // Contenu principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = "Bienvenue au Zoo de la Barben 🐾",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = colorResource(id = R.color.LightBrown),
                modifier = Modifier.padding(top = 48.dp, bottom = 16.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.zoo_la_barben_0),
                contentDescription = "Image d'accueil du zoo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Text(
                text = "Découvrez les animaux, explorez les enclos, laissez un avis, et vivez une aventure inoubliable !",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                color = Color.DarkGray,
                modifier = Modifier.padding(16.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {navController.navigate("enclosures_screen") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.Brown))
                ) {
                    Text("Explorer les enclos")
                }

                Button(
                    onClick = {navController.navigate("map") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.LightBrown))
                ) {
                    Text("Voir le plan du parc")
                }
            }
        }
    }
}

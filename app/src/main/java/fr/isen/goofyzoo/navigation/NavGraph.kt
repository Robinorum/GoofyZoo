package fr.isen.goofyzoo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import fr.isen.goofyzoo.models.Biome
import fr.isen.goofyzoo.screens.biomes.BiomeDetailScreen
import fr.isen.goofyzoo.screens.biomes.BiomeListScreen
import fr.isen.goofyzoo.screens.enclosures.AnimalDetailScreen
import fr.isen.goofyzoo.screens.enclosures.EnclosureDetailScreen
import fr.isen.goofyzoo.screens.enclosures.EnclosuresScreen
import fr.isen.goofyzoo.screens.main.HomePage
import fr.isen.goofyzoo.screens.main.MapPage
import fr.isen.goofyzoo.screens.main.NavigationScreen
import fr.isen.goofyzoo.screens.main.ReviewsScreen
import fr.isen.goofyzoo.screens.main.ServicesScreen


@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "home", modifier = modifier) {
        composable("home") { HomePage() }
        composable("map") { MapPage() }
        composable("services_screen") { ServicesScreen() }
        composable("reviews_screen") { ReviewsScreen() }
        composable("navigation_screen") { NavigationScreen() }

        composable("biomes_list") { BiomeListScreen(navController) }
        composable("biome_detail") { backStackEntry ->
            BiomeDetailScreen(navController)
        }
        composable("enclosures") { EnclosuresScreen() }
        composable("enclosure_detail") { backStackEntry ->
            EnclosureDetailScreen(navController)
        }


    }
}
package fr.isen.goofyzoo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import fr.isen.goofyzoo.screens.admin.AdminDashboardScreen
import fr.isen.goofyzoo.screens.admin.FeedingScheduleScreen
import fr.isen.goofyzoo.screens.admin.MaintenanceScreen
import fr.isen.goofyzoo.screens.biomes.BiomeDetailScreen
import fr.isen.goofyzoo.screens.biomes.BiomeListScreen
import fr.isen.goofyzoo.screens.enclosures.AnimalDetailScreen
import fr.isen.goofyzoo.screens.enclosures.EnclosureDetailScreen
import fr.isen.goofyzoo.screens.enclosures.EnclosuresScreen
import fr.isen.goofyzoo.screens.main.HomePage
import fr.isen.goofyzoo.screens.main.NavigationScreen
import fr.isen.goofyzoo.screens.main.ReviewsScreen
import fr.isen.goofyzoo.screens.main.ServicesScreen

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "home", modifier = modifier) {
        composable("home") { HomePage() }
        composable("services_screen") { ServicesScreen() }
        composable("reviews_screen") { ReviewsScreen() }
        composable("navigation_screen") { NavigationScreen() }

        composable("biomes_list") { BiomeListScreen(navController) }
        composable("biome_detail/{biomeId}") { backStackEntry ->
            BiomeDetailScreen(navController, backStackEntry.arguments?.getString("biomeId"))
        }
        composable("enclosures") { EnclosuresScreen() }
        composable("enclosure_detail/{enclosureId}") { backStackEntry ->
            EnclosureDetailScreen(navController, backStackEntry.arguments?.getString("enclosureId"))
        }
        composable("animal_detail/{animalId}") { backStackEntry ->
            AnimalDetailScreen(backStackEntry.arguments?.getString("animalId"))
        }

        composable("admin_dashboard_screen") { AdminDashboardScreen(navController) }
        composable("maintenance_screen") { MaintenanceScreen() }
        composable("feeding_schedule_screen") { FeedingScheduleScreen() }

    }
}
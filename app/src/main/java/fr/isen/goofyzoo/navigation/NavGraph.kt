package fr.isen.goofyzoo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import fr.isen.goofyzoo.screens.enclosures.EnclosuresListScreen
import fr.isen.goofyzoo.screens.enclosures.EnclosureDetailScreen
import fr.isen.goofyzoo.screens.main.HomePage
import fr.isen.goofyzoo.screens.main.MapPage
import fr.isen.goofyzoo.screens.main.NavigationScreen
import fr.isen.goofyzoo.screens.services.ServiceDetailScreen
import fr.isen.goofyzoo.screens.services.ServicesListScreen
import fr.isen.goofyzoo.screens.profil.ProfileScreen

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier, userId: String, username: String) {
    NavHost(navController = navController, startDestination = "home", modifier = modifier) {
        composable("home") { HomePage(navController) }
        composable("profile_screen") { ProfileScreen(navController,userId) }
        composable("map") { MapPage() }

        composable("enclosures_screen") { EnclosuresListScreen(navController) }
        composable("enclosure_detail") { backStackEntry ->
            EnclosureDetailScreen(navController, userId, username)
        }

        composable("services_screen") { ServicesListScreen(navController) }
        composable("service_detail") { backStackEntry ->
            ServiceDetailScreen(navController)
        }

        composable("navigation_screen") { NavigationScreen() }

    }
}
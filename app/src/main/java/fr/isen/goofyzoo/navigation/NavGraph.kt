package fr.isen.goofyzoo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import fr.isen.goofyzoo.screens.main.HomePage
import fr.isen.goofyzoo.screens.main.OtherPage
import fr.isen.goofyzoo.screens.main.MapPage

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "home", modifier = modifier) {
        composable("home") { HomePage() }
        composable("map") { MapPage() }
        composable("other") { OtherPage() }
    }
}

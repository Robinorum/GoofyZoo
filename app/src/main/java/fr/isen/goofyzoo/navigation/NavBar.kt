package fr.isen.goofyzoo.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import fr.isen.goofyzoo.R

@Composable
fun BottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.substringBefore("/{") ?: navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.home), contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.map), contentDescription = "Map") },
            label = {Text("Map")},
            selected = currentRoute == "map",
            onClick = { navController.navigate("map")}
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.jsp), contentDescription = "Biomes") },
            label = { Text("Biomes") },
            selected = currentRoute == "biomes_list",
            onClick = { navController.navigate("biomes_list") }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.jsp), contentDescription = "Services") },
            label = { Text("Services") },
            selected = currentRoute == "services_screen",
            onClick = { navController.navigate("services_screen") }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.jsp), contentDescription = "Navigation") },
            label = { Text("Navigation") },
            selected = currentRoute == "navigation_screen",
            onClick = { navController.navigate("navigation_screen") }
        )
    }
}





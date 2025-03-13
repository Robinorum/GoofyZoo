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
            icon = { Icon(painterResource(id = R.drawable.jsp), contentDescription = "Unknown") },
            label = { Text("Unknown") },
            selected = currentRoute == "other",
            onClick = { navController.navigate("other") }
        )
    }
}




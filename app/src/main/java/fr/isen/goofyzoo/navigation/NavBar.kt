package fr.isen.goofyzoo.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
            label = { Text(stringResource(R.string.navbar_champ1)) },
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.map), contentDescription = "Map") },
            label = {Text(stringResource(R.string.navbar_champ2))},
            selected = currentRoute == "map",
            onClick = { navController.navigate("map")}
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.rabbit), contentDescription = "Biomes") },
            label = { Text(stringResource(R.string.navbar_champ3)) },
            selected = currentRoute == "enclosures_screen",
            onClick = { navController.navigate("enclosures_screen") }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.cafe), contentDescription = "Services") },
            label = { Text(stringResource(R.string.navbar_champ4)) },
            selected = currentRoute == "services_screen",
            onClick = { navController.navigate("services_screen") }
        )

    }
}





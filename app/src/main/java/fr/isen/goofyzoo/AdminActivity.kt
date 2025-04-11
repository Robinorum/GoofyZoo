package fr.isen.goofyzoo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.isen.goofyzoo.screens.admin.AdminDashboardScreen
import fr.isen.goofyzoo.screens.admin.FeedingScheduleScreen
import fr.isen.goofyzoo.screens.admin.MaintenanceScreen

class AdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = "admin_dashboard_screen") {
                composable("admin_dashboard_screen") { AdminDashboardScreen(navController) }
                composable("maintenance_screen") { MaintenanceScreen(navController) }
                composable("feeding_schedule_screen") { FeedingScheduleScreen(navController) }
            }
        }
    }
}
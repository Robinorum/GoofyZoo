package fr.isen.goofyzoo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.isen.goofyzoo.screens.employee.AllSchedule
import fr.isen.goofyzoo.screens.employee.EmployeeDashboardScreen


class EmployeeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = "employee_dashboard_screen") {
                composable("employee_dashboard_screen") { EmployeeDashboardScreen(navController) }
                composable("all_schedule") { AllSchedule() }
            }
        }
    }
}
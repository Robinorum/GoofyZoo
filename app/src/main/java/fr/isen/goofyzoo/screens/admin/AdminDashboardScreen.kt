package fr.isen.goofyzoo.screens.admin

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.isen.goofyzoo.AuthActivity
import fr.isen.goofyzoo.R

@Composable
fun AdminDashboardScreen(navController: NavController) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo du Zoo",
                modifier = Modifier
                    .size(120.dp)
                    .weight(1f, fill = false)
            )


            IconButton(
                onClick = {

                    val intent = Intent(context, AuthActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.logout),
                    contentDescription = stringResource(R.string.logout_desc),
                    tint = Color.Unspecified,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Titre
        Text(
            text = stringResource(R.string.admin_welcome),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )


        Button(
            onClick = { navController.navigate("maintenance_screen") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Icon(
                painter = painterResource(id = R.drawable.hammer),
                contentDescription = stringResource(R.string.admin_button1_desc),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.admin_button1), color = Color.White)
        }


        Button(
            onClick = { navController.navigate("feeding_schedule_screen") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
        ) {
            Icon(
                painter = painterResource(id = R.drawable.burger),
                contentDescription = stringResource(R.string.admin_button2_desc),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.admin_button2), color = Color.White)
        }
    }
}
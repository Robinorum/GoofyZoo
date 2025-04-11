package fr.isen.goofyzoo.screens.employee

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.database.*
import fr.isen.goofyzoo.AuthActivity
import fr.isen.goofyzoo.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun EmployeeDashboardScreen(navController: NavController) {
    val database = FirebaseDatabase.getInstance().getReference("zoo")
    var enclosuresToFeed by remember { mutableStateOf<List<Triple<String, String, String>>>(emptyList()) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                val now = Calendar.getInstance()
                val upcomingList = mutableListOf<Triple<String, String, String>>()

                snapshot.children.forEach { biomeSnap ->
                    val biomeName = biomeSnap.child("name").getValue(String::class.java)
                    if (biomeName != null) {
                        val enclosuresSnap = biomeSnap.child("enclosures")
                        enclosuresSnap.children.forEach { encSnap ->
                            val id = encSnap.child("id").getValue(String::class.java)
                            val meal = encSnap.child("meal").getValue(String::class.java)
                            if (!id.isNullOrEmpty() && !meal.isNullOrEmpty()) {
                                try {
                                    val mealDate = sdf.parse(meal)
                                    if (mealDate != null) {
                                        val mealCal = Calendar.getInstance().apply {
                                            time = mealDate
                                            set(Calendar.YEAR, now.get(Calendar.YEAR))
                                            set(Calendar.MONTH, now.get(Calendar.MONTH))
                                            set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))
                                        }
                                        if (mealCal.after(now)) {
                                            upcomingList.add(Triple(biomeName, id, meal))
                                        }
                                    }
                                } catch (_: Exception) {}
                            }
                        }
                    }
                }

                upcomingList.sortBy { (_, _, meal) ->
                    val cal = Calendar.getInstance().apply {
                        time = sdf.parse(meal)!!
                        set(Calendar.YEAR, now.get(Calendar.YEAR))
                        set(Calendar.MONTH, now.get(Calendar.MONTH))
                        set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))
                    }
                    cal.timeInMillis
                }

                enclosuresToFeed = upcomingList
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase error: ${error.message}")
            }
        })
    }

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


        Text(
            text = stringResource(R.string.employee_welcome),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )


        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (enclosuresToFeed.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.empty_feeding),
                        color = Color.Gray,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(top = 32.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(enclosuresToFeed) { (biome, id, meal) ->
                    FeedingInfoCard(biomeName = biome, enclosureId = id, mealTime = meal)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }


        Button(
            onClick = { navController.navigate("all_schedule") },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = stringResource(R.string.employee_button1),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FeedingInfoCard(biomeName: String, enclosureId: String, mealTime: String) {
    val sdf = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val now = Calendar.getInstance()
    val mealCal = Calendar.getInstance()
    val remainingTime = try {
        val parsed = sdf.parse(mealTime)
        if (parsed != null) {
            mealCal.time = parsed
            mealCal.set(Calendar.YEAR, now.get(Calendar.YEAR))
            mealCal.set(Calendar.MONTH, now.get(Calendar.MONTH))
            mealCal.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))

            val diff = mealCal.timeInMillis - now.timeInMillis
            if (diff > 0) {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
                "Dans ${hours}h ${minutes}min"
            } else {
                "Heure passée"
            }
        } else "Heure invalide"
    } catch (e: Exception) {
        "Erreur"
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Biome : $biomeName", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Enclos n°$enclosureId", fontSize = 16.sp)
            Text("Nourrissage à : $mealTime", fontSize = 16.sp)
            if (!remainingTime.contains("passée")) {
                Text(remainingTime, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
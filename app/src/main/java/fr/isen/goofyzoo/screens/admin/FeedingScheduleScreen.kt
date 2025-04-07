package fr.isen.goofyzoo.screens.admin

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.*
import fr.isen.goofyzoo.R
import fr.isen.goofyzoo.models.Biome
import fr.isen.goofyzoo.models.Enclosure
import java.util.*

@Composable
fun FeedingScheduleScreen() {
    val database = FirebaseDatabase.getInstance().getReference("zoo")
    var biomes by remember { mutableStateOf<List<Biome>>(emptyList()) }
    var expandedBiomeId by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val biomeList = snapshot.children.mapNotNull { it.getValue(Biome::class.java) }
                biomes = biomeList
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase error: ${error.message}")
            }
        })
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Gestion des horaires de nourrissage",
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 26.sp),
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(biomes) { biome ->
                BiomeFeedingItem(
                    biome = biome,
                    isExpanded = expandedBiomeId == biome.id,
                    onExpandClick = {
                        expandedBiomeId = if (expandedBiomeId == biome.id) null else biome.id
                    },
                    onTimePicked = { enclosure, time ->
                        updateFeedingTimeInDb(database, biome.id, enclosure.id, time)
                    },
                    context = context
                )
            }
        }
    }
}

@Composable
fun BiomeFeedingItem(
    biome: Biome,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    onTimePicked: (Enclosure, String) -> Unit,
    context: Context
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpandClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(android.graphics.Color.parseColor(biome.color)))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = biome.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = if (isExpanded) "▲" else "▼",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                biome.enclosures.forEach { enclosure ->
                    FeedingItem(enclosure = enclosure, onTimePicked = { time ->
                        onTimePicked(enclosure, time)
                    }, context = context)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun FeedingItem(enclosure: Enclosure, onTimePicked: (String) -> Unit, context: Context) {
    var timeText by remember { mutableStateOf(enclosure.meal) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFE8F5E9)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Enclos n°${enclosure.id}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                )
                Text(
                    buildAnnotatedString {
                        append("Heure : ")
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF388E3C)
                            )
                        ) {
                            append(timeText)
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                )
            }



            Icon(
                painter = painterResource(id = R.drawable.clock),
                contentDescription = "Définir l'heure",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        val cal = Calendar.getInstance()
                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                val pickedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                                timeText = pickedTime
                                onTimePicked(pickedTime)
                            },
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                tint = Color.Gray
            )

        }
    }
}

fun updateFeedingTimeInDb(database: DatabaseReference, biomeId: String, enclosureId: String, time: String) {
    database.get().addOnSuccessListener { snapshot ->
        for (biomeSnapshot in snapshot.children) {
            if (biomeSnapshot.child("id").getValue(String::class.java) == biomeId) {
                for (enclosureSnapshot in biomeSnapshot.child("enclosures").children) {
                    if (enclosureSnapshot.child("id").getValue(String::class.java) == enclosureId) {
                        enclosureSnapshot.ref.child("meal").setValue(time)
                        return@addOnSuccessListener
                    }
                }
            }
        }
    }.addOnFailureListener {
        println("Erreur de mise à jour de l'heure de nourrissage : ${it.message}")
    }
}

package fr.isen.goofyzoo.screens.admin

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.goofyzoo.models.Biome
import fr.isen.goofyzoo.models.Enclosure

@Composable
fun MaintenanceScreen() {
    val database = FirebaseDatabase.getInstance().getReference("zoo")
    var biomes by remember { mutableStateOf<List<Biome>>(emptyList()) }
    var expandedBiomeId by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val biomeList = snapshot.children.mapNotNull { it.getValue(Biome::class.java) }
                biomes = biomeList
            }

            override fun onCancelled(error: DatabaseError) {
                println("Erreur Firebase: ${error.message}")
            }
        })
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Gestion de la maintenance des enclos",
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 26.sp),
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(biomes) { biome ->
                BiomeMaintenanceItem(
                    biome = biome,
                    isExpanded = expandedBiomeId == biome.id,
                    onExpandClick = {
                        expandedBiomeId = if (expandedBiomeId == biome.id) null else biome.id
                    },
                    onToggleEnclosureState = { enclosure, newState ->
                        database.get().addOnSuccessListener { snapshot ->
                            for (biomeSnapshot in snapshot.children) {
                                val biomeId = biomeSnapshot.child("id").getValue(String::class.java)
                                if (biomeId == biome.id) {
                                    for (enclosureSnapshot in biomeSnapshot.child("enclosures").children) {
                                        val enclosureId = enclosureSnapshot.child("id").getValue(String::class.java)
                                        if (enclosureId == enclosure.id.toString()) {
                                            enclosureSnapshot.ref.child("is_open").setValue(newState)
                                            return@addOnSuccessListener
                                        }
                                    }
                                }
                            }
                        }.addOnFailureListener { error ->
                            println("Erreur lors de la mise à jour: ${error.message}")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BiomeMaintenanceItem(
    biome: Biome,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    onToggleEnclosureState: (Enclosure, Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpandClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(android.graphics.Color.parseColor(biome.color)))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = biome.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                    color = Color.White
                )
                Text(
                    text = if (isExpanded) "▲" else "▼",
                    color = Color.White,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                biome.enclosures.forEach { enclosure ->
                    EnclosureMaintenanceItem(
                        enclosure = enclosure,
                        onToggleState = { newState ->
                            onToggleEnclosureState(enclosure, newState)
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun EnclosureMaintenanceItem(
    enclosure: Enclosure,
    onToggleState: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF5F5F5)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Enclos n°${enclosure.id}",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
            )
            Switch(
                checked = enclosure.is_open,
                onCheckedChange = { newState ->
                    onToggleState(newState)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Green,
                    checkedTrackColor = Color.Green.copy(alpha = 0.5f),
                    uncheckedThumbColor = Color.Red,
                    uncheckedTrackColor = Color.Red.copy(alpha = 0.5f)
                )
            )
        }
    }
}
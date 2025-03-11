package fr.isen.goofyzoo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.database.*
import fr.isen.goofyzoo.model.User
import fr.isen.goofyzoo.ui.theme.GoofyZooTheme


class MainActivity : ComponentActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        database = FirebaseDatabase.getInstance().getReference("users")

        setContent {
            GoofyZooTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(database, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(database: DatabaseReference, modifier: Modifier = Modifier) {
    var message by remember { mutableStateOf("Appuyez sur un bouton") }
    var userList by remember { mutableStateOf(listOf<User>()) }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = {
                addUser(database, "2", "Alice Dupont", "alice@example.com") {
                    message = if (it) "Utilisateur ajouté !" else "Erreur d'ajout."
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Ajouter un utilisateur")
        }

        Button(
            onClick = {
                readUsers(database) { users ->
                    userList = users
                    message = "Utilisateurs récupérés ! Voir logs."
                    users.forEach { Log.d("FirebaseDB", "Utilisateur : $it") }
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Lire les utilisateurs")
        }
    }
}

fun addUser(database: DatabaseReference, id: String, name: String, email: String, callback: (Boolean) -> Unit) {
    val user = User(id, name, email)
    database.child("users").child(id).setValue("test")
        .addOnSuccessListener {
            callback(true)
        }
        .addOnFailureListener { callback(false) }
}

fun readUsers(database: DatabaseReference, callback: (List<User>) -> Unit) {
    database.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val users = mutableListOf<User>()
            for (userSnapshot in snapshot.children) {
                val user = userSnapshot.getValue(User::class.java)
                user?.let { users.add(it) }
            }
            callback(users)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("FirebaseDB", "Erreur de lecture", error.toException())
        }
    })
}

@Preview(showBackground = true)
@Composable
fun PreviewScreen() {
    GoofyZooTheme {
        MainScreen(FirebaseDatabase.getInstance().getReference("users"))
    }
}

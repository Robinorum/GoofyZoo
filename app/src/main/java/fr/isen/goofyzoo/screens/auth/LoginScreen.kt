package fr.isen.goofyzoo.screens.auth

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import fr.isen.goofyzoo.MainActivity
import fr.isen.goofyzoo.AdminActivity
import fr.isen.goofyzoo.EmployeeActivity
import fr.isen.goofyzoo.R

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().reference

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo de l'application",
            modifier = Modifier
                .height(120.dp)
                .padding(bottom = 24.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = stringResource(id = R.string.log_bienvenue),
            color = colorResource(id = R.color.Brown),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = {
                Text(
                    text = stringResource(id = R.string.log_champ1),
                    color = colorResource(id = R.color.Brown)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {
                Text(
                    text = stringResource(id = R.string.log_champ2),
                    color = colorResource(id = R.color.Brown)
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val userId = user?.uid ?: return@addOnCompleteListener

                            database.child("users").child(userId).get()
                                .addOnSuccessListener { snapshot ->
                                    val isAdmin = snapshot.child("admin").getValue(Boolean::class.java) ?: false
                                    val isEmployee = snapshot.child("employee").getValue(Boolean::class.java) ?: false
                                    val username = snapshot.child("username").getValue(String::class.java) ?: ""

                                    val intent = when {
                                        isAdmin -> Intent(navController.context, AdminActivity::class.java)
                                        isEmployee -> Intent(navController.context, EmployeeActivity::class.java)
                                        else -> Intent(navController.context, MainActivity::class.java)
                                    }

                                    intent.putExtra("UserId", userId)
                                    intent.putExtra("Username", username)
                                    navController.context.startActivity(intent)
                                }
                        } else {
                            errorMessage = task.exception?.localizedMessage ?: "Erreur inconnue"
                        }
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.Saffron)
            )
        ) {
            Text(
                text = stringResource(id = R.string.log_connexion),
                color = colorResource(id = R.color.Brown)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = { navController.navigate("register") }) {
            Text(
                text = stringResource(id = R.string.log_inscription),
                color = colorResource(id = R.color.Brown)
            )
        }
    }
}

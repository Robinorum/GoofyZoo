package fr.isen.goofyzoo.screens.auth

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
import fr.isen.goofyzoo.R
import fr.isen.goofyzoo.models.User

@Composable
fun RegisterScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().reference

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        val pattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\|,.<>/?]).{7,}$"
        return password.matches(pattern.toRegex())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .height(120.dp)
                .padding(bottom = 24.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = stringResource(R.string.reg_bienvenue),
            color = colorResource(id = R.color.Brown),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(R.string.reg_champ1), color = colorResource(id = R.color.Brown)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.reg_champ2), color = colorResource(id = R.color.Brown)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(id= R.string.reg_champ3), color = colorResource(id = R.color.Brown)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (email.isEmpty() || !isValidEmail(email)) {
                    errorMessage = "Veuillez entrer une adresse email valide."
                    return@Button
                }

                if (password.isEmpty() || !isValidPassword(password)) {
                    errorMessage = "Mot de passe trop faible. Il doit contenir au moins 7 caractères, une majuscule, un chiffre et un caractère spécial."
                    return@Button
                }

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val userId = user?.uid ?: return@addOnCompleteListener

                            val userData =  User(
                                id = userId,
                                username = username,
                                email = email,
                                admin = false,
                                employee = false
                            )

                            database.child("users").child(userId).setValue(userData)
                                .addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        navController.navigate("login")
                                    } else {
                                        errorMessage = dbTask.exception?.localizedMessage ?: "Erreur lors de l'enregistrement"
                                    }
                                }
                        } else {
                            errorMessage = task.exception?.localizedMessage ?: "Erreur d'inscription"
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
                text = stringResource( R.string.reg_inscription),
                color = colorResource(id = R.color.Brown)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(onClick = { navController.navigate("login") }) {
            Text(
                text = stringResource(id = R.string.reg_connexion),
                color = colorResource(id = R.color.Brown)
            )
        }
    }
}

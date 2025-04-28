package com.example.catdex

import MainActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.catdex.ui.theme.CatdexTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialiser Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Vérifier si l'utilisateur est déjà connecté
        if (auth.currentUser != null) {
            navigateToMainActivity()
            return
        }

        setContent {
            CatdexTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen()
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    @Composable
    fun LoginScreen() {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var username by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        var isSignUpMode by remember { mutableStateOf(false) }
        val context = LocalContext.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bienvenue sur Catdex!",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mot de passe") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            if (isSignUpMode) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Nom d'utilisateur") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (email.isEmpty() || password.isEmpty() || (isSignUpMode && username.isEmpty())) {
                        Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true

                    if (isSignUpMode) {
                        // Inscription
                        signUp(email, password, username) { success, message ->
                            if (success) navigateToMainActivity() else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                isLoading = false
                            }
                        }
                    } else {
                        // Connexion
                        signIn(email, password) { success, message ->
                            if (success) navigateToMainActivity() else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                isLoading = false
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(text = if (isSignUpMode) "S'inscrire" else "Se connecter")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { isSignUpMode = !isSignUpMode }) {
                Text(text = if (isSignUpMode) "Déjà un compte? Se connecter" else "Pas de compte? S'inscrire")
            }
        }
    }

    private fun signUp(email: String, password: String, username: String, callback: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = hashMapOf(
                        "uid" to auth.currentUser?.uid,
                        "email" to email,
                        "username" to username
                    )

                    db.collection("users")
                        .document(auth.currentUser?.uid!!)
                        .set(user)
                        .addOnSuccessListener {
                            callback(true, "Compte créé avec succès!")
                        }
                        .addOnFailureListener { e ->
                            callback(false, "Erreur base de données: ${e.message}")
                        }
                } else {
                    callback(false, "Erreur d'inscription: ${task.exception?.message}")
                }
            }
    }

    private fun signIn(email: String, password: String, callback: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Connexion réussie")
                } else {
                    callback(false, "Erreur de connexion: ${task.exception?.message}")
                }
            }
    }
}
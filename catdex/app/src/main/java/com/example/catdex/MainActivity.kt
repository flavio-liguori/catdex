package com.example.catdex

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.catdex.entities.User
import com.example.catdex.ui.theme.CatdexTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private lateinit var db: FirebaseFirestore
private lateinit var auth: FirebaseAuth;


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialise Firebase
        FirebaseApp.initializeApp(this)

        setContentView(R.layout.activity_main)

        // Maintenant tu peux utiliser FirebaseFirestore
        val db = FirebaseFirestore.getInstance()
        val user = User(uid = "1234", email = "test@example.com", username = "TestUser")



        auth = FirebaseAuth.getInstance()
        // ...
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CatdexTheme {
        Greeting("Android")
    }
}
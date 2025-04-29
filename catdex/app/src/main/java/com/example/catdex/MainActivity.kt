package com.example.catdex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.catdex.ui.theme.CatdexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CatdexTheme {
                HomePage()
            }
        }
    }
}

@Composable
fun HomePage() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Avatar rond du haut
                Image(
                    painter = painterResource(id = R.drawable.ic_cat_avatar),
                    contentDescription = "Avatar chat",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Image du chat
                Image(
                    painter = painterResource(id = R.drawable.cat_pixel_art),
                    contentDescription = "Chat pixel",
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .padding(8.dp)
                )

                Text(
                    text = "Profil chat",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Bouton encadré style "carrousel"
                OutlinedButton(onClick = { /* action carrousel */ }) {
                    Text("Carrousel chat possédé", color = Color.Black)
                }
            }
// Barre de navigation en bas (corrigée)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* appareil photo */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera),
                        contentDescription = "Appareil photo"
                    )
                }
                IconButton(onClick = { /* accueil - patte remplacée par cat avatar */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cat_avatar),
                        contentDescription = "Accueil"
                    )
                }
                IconButton(onClick = { /* profil */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_user),
                        contentDescription = "Profil"
                    )
                }
            }

        }
    }
}

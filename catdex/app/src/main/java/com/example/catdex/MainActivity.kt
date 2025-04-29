package com.example.catdex

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.catdex.ui.theme.CatdexTheme
import com.google.accompanist.pager.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CatdexTheme {
                HomeWithSwipePages()
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeWithSwipePages() {
    val pagerState = rememberPagerState(initialPage = 1) // HomePage par défaut
    val scope = rememberCoroutineScope()

    HorizontalPager(
        count = 3, // 3 pages maintenant
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        when (page) {
            0 -> CameraPage()
            1 -> HomePage(
                onCameraClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                onUserClick = { scope.launch { pagerState.animateScrollToPage(2) } }
            )
            2 -> UserPage()
        }
    }
}
@Composable
fun HomePage(onCameraClick: () -> Unit, onUserClick: () -> Unit) {
    val backgroundColor = Color(0xFFF8F3E9)
    val accentColor = Color(0xFFFF9D72)
    val borderColor = Color(0xFF513E31)
    val cardColor = Color.White
    val primaryColor = Color(0xFF5D4037)

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(backgroundColor, Color(0xFFF0E8DB))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Cat", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Text("Dex", fontSize = 24.sp, fontWeight = FontWeight.Light, color = accentColor)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0x40513E31)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = backgroundColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        var isAvatarClicked by remember { mutableStateOf(false) }
                        val avatarScale by animateFloatAsState(
                            targetValue = if (isAvatarClicked) 1.2f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "avatarScale"
                        )

                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .shadow(6.dp, CircleShape)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(Color(0xFFFFFBF5), Color(0xFFF0F0F0))
                                    )
                                )
                                .border(1.dp, borderColor.copy(alpha = 0.5f), CircleShape)
                                .scale(avatarScale),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_cat_avatar),
                                contentDescription = "Avatar chat",
                                modifier = Modifier.size(45.dp).padding(4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0x30513E31))
                                .border(0.5.dp, borderColor.copy(alpha = 0.2f), RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color(0xFF9ED8FF), Color(0xFF96CCEF))
                                            )
                                        )
                                ) {
                                    val sunScale by animateFloatAsState(
                                        targetValue = 1f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        ),
                                        label = "sunScale"
                                    )

                                    Box(
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .size(28.dp)
                                            .scale(sunScale)
                                            .shadow(2.dp, CircleShape)
                                            .background(
                                                brush = Brush.radialGradient(
                                                    colors = listOf(
                                                        Color(0xFFFFF6D6),
                                                        Color(0xFFFFDF6C)
                                                    )
                                                ),
                                                shape = CircleShape
                                            )
                                            .align(Alignment.TopStart)
                                    )

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .align(Alignment.BottomCenter)
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(Color(0xFF9DD689), Color(0xFF8BC979))
                                                )
                                            )
                                    )
                                }

                                var catScale by remember { mutableStateOf(1f) }
                                val animatedCatScale by animateFloatAsState(
                                    targetValue = catScale,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    ),
                                    label = "catScale"
                                )

                                Image(
                                    painter = painterResource(id = R.drawable.cat_pixel_art),
                                    contentDescription = "Chat pixel",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .scale(animatedCatScale)
                                        .align(Alignment.Center),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            modifier = Modifier.padding(vertical = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = accentColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "Profil chat",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = primaryColor,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { /* action carrousel */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .shadow(3.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = cardColor),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_cat_avatar),
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Carrousel chat possédé",
                                    color = primaryColor,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(vertical = 8.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0x40513E31)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onCameraClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera),
                            contentDescription = "Appareil photo",
                            tint = borderColor
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cat_avatar),
                            contentDescription = "Accueil",
                            tint = accentColor
                        )
                    }
                    IconButton(onClick = onUserClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_user),
                            contentDescription = "Profil utilisateur",
                            tint = borderColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CameraPage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Page Appareil Photo - à implémenter", color = Color.Black)
    }
}

@Composable
fun UserPage() {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Page Profil Utilisateur",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B6B),
                    contentColor = Color.White
                )
            ) {
                Text("Se déconnecter")
            }
        }
    }
}


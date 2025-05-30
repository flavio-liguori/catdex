package com.example.catdex
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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
import coil.compose.AsyncImage
import com.example.catdex.ui.theme.CatdexTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

// --- modèle et données
data class CatBreed(
    val name: String,
    val imageUrl: String,
    val pawCount: Int,
    val catCount: Int
)

val sampleBreeds = listOf(
    CatBreed("Abyssinian",      "https://placekitten.com/300/300?image=1",  pawCount = 2, catCount = 1),
    CatBreed("American Bobtail","https://placekitten.com/300/300?image=2",  pawCount = 2, catCount = 1),
    CatBreed("American Shorthair","https://placekitten.com/300/300?image=3",pawCount = 2, catCount = 1),
    CatBreed("American Shorthair","https://placekitten.com/300/300?image=4",pawCount = 0, catCount = 3),
    CatBreed("Bengal",           "https://placekitten.com/300/300?image=5",pawCount = 2, catCount = 2),
    CatBreed("British Shorthair","https://placekitten.com/300/300?image=6",pawCount = 2, catCount = 1),
    CatBreed("Cornish Rex",      "https://placekitten.com/300/300?image=7",pawCount = 2, catCount = 1),
    CatBreed("Egyptian Mau",     "https://placekitten.com/300/300?image=8",pawCount = 1, catCount = 2),
    CatBreed("Devon Rex",        "https://placekitten.com/300/300?image=9",pawCount = 2, catCount = 1),
    CatBreed("Exotic",           "https://placekitten.com/300/300?image=10",pawCount = 2, catCount = 1),
    // … autres races
)

// --- Activity principale
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
    val pagerState = rememberPagerState(initialPage = 1)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Colonne générale : pager en haut, barre nav en bas
    Column(modifier = Modifier.fillMaxSize()) {
        // 1) Contenu défilable
        Box(modifier = Modifier.weight(1f)) {
            HorizontalPager(
                count = 4,  // 0=Camera,1=Home,2=User,3=CatDex
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> CameraPage()
                    1 -> HomePage(
                        onCameraClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                        onUserClick   = { scope.launch { pagerState.animateScrollToPage(2) } },
                        onBreedsClick = { scope.launch { pagerState.animateScrollToPage(3) } }
                    )
                    2 -> UserPage()
                    3 -> CatDexScreen(
                        breeds = sampleBreeds,
                        onBreedClick = { breed ->
                            Intent(context, DetailActivity::class.java).also { intent ->
                                intent.putExtra("breedName", breed.name)
                                context.startActivity(intent)
                            }
                        }
                    )
                }
            }
        }

        // 2) Barre de navigation persistante
        BottomNavBar(
            onCameraClick = { scope.launch { pagerState.animateScrollToPage(0) } },
            onHomeClick   = { scope.launch { pagerState.animateScrollToPage(1) } },
            onUserClick   = { scope.launch { pagerState.animateScrollToPage(2) } }
        )
    }
}

// Barre basse utilisée globalement
@Composable
fun BottomNavBar(
    onCameraClick: () -> Unit,
    onHomeClick: () -> Unit,
    onUserClick: () -> Unit
) {
    val borderColor = Color(0xFF513E31)
    val accentColor = Color(0xFFFF9D72)
    val cardColor   = Color.White

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
            IconButton(onClick = onHomeClick) {
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

// --- Reste de tes écrans inchangé (HomePage, CatDexScreen, BreedCard, CameraPage, UserPage, DetailActivity, DetailScreen)
// tu peux simplement conserver les définitions que tu avais, elles fonctionneront exactement de la même façon.

@Composable
fun HomePage(
    onCameraClick: () -> Unit,
    onUserClick: () -> Unit,
    onBreedsClick: () -> Unit
) {
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
                                .clickable { onBreedsClick() }
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
                                modifier = Modifier
                                    .size(45.dp)
                                    .padding(4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .shadow(
                                    4.dp,
                                    RoundedCornerShape(20.dp),
                                    spotColor = Color(0x30513E31)
                                )
                                .border(
                                    0.5.dp,
                                    borderColor.copy(alpha = 0.2f),
                                    RoundedCornerShape(20.dp)
                                ),
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
        }
    }
}

@Composable
fun CatDexScreen(
    breeds: List<CatBreed>,
    onBreedClick: (CatBreed) -> Unit
) {
    var search by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp)
    ) {
        Text(
            text = "CatDex",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Rechercher") },
            placeholder = { Text("Search") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(horizontal = 8.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(breeds.filter { it.name.contains(search, ignoreCase = true) }) { breed ->
                BreedCard(breed = breed, onClick = { onBreedClick(breed) })
            }
        }
    }
}

@Composable
fun BreedCard(
    breed: CatBreed,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = breed.imageUrl,
                contentDescription = "${breed.name} photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color(0xFFE0E0E0), CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = breed.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    repeat(breed.pawCount) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Patte",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                    }
                    if (breed.pawCount > 0 && breed.catCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    repeat(breed.catCount) {
                        Icon(
                            painter = painterResource(id = R.drawable.cat),
                            contentDescription = "Silhouette chat",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CameraPage() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 1) Gestion de la permission caméra
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasPermission = granted }
    )
    LaunchedEffect(Unit) {
        if (!hasPermission) launcher.launch(Manifest.permission.CAMERA)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 2) Si ok, affiche le flux CameraX
        if (hasPermission) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val selector = CameraSelector.DEFAULT_BACK_CAMERA
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview)
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                }
            )
        } else {
            // si on n'a pas la permission
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Autorisation caméra requise", color = Color.White)
            }
        }

        // 3) Cadre vert + overlay chat
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.Center)
                .border(4.dp, Color(0xFF4CAF50), RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            // ici tu peux mettre un Icon ou painterResource d’un chat stylisé
            Icon(
                imageVector = Icons.Default.Info, // remplace par ton overlay chat
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(128.dp)
            )
        }

        // 4) Conseil et bouton prise de vue
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Click on ○ to learn what the result means.",
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            IconButton(
                onClick = { /* TODO: lancer l’analyse du frame */ },
                modifier = Modifier
                    .size(72.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Prendre photo",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        // 5) Footer : logo + bouton info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // logo sylvester.ai (ajoute un painterResource si tu as le drawable)
            Text("sylvester.ai", color = Color.White)
            IconButton(onClick = { /* TODO: afficher aide */ }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Aide",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun UserPage() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- Avatar centré en haut (placeholder Material)
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(4.dp, Color(0xFF2E7D32), CircleShape)
                .background(Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Avatar chat",
                modifier = Modifier.size(64.dp),
                tint = Color(0xFF2E7D32)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Titre
        Text(
            text = "MEDALS",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF2E7D32),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Grille de médailles (placeholder EmojiEvents)
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // on affiche 20 cases avec la même icône EmojiEvents
            items(20) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .border(2.dp, Color(0xFF81C784), CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFF2E7D32)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Liste des tâches
        val tasks = listOf("Read 5 pages", "Walk 1 km", "Clean the kitchen")
        tasks.forEach { task ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = false,
                    onCheckedChange = {},
                    colors = CheckboxDefaults.colors(
                        uncheckedColor = Color(0xFF81C784),
                        checkmarkColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = task, style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // --- Bouton “Se déconnecter”
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                Intent(context, LoginActivity::class.java).also { intent ->
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD32F2F),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Se déconnecter")
        }
    }
}


class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val breedName = intent.getStringExtra("breedName") ?: return
        val breed = sampleBreeds.firstOrNull { it.name == breedName } ?: return

        setContent {
            CatdexTheme {
                DetailScreen(breed = breed) { finish() }
            }
        }
    }
}

@Composable
fun DetailScreen(breed: CatBreed, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = breed.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 250.dp),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Retour"
                        )
                    }
                    Text(
                        text = breed.name,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { /* like */ }) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Favori",
                            tint = Color(0xFFFF6B6B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Column {
                            Text("Compatibility", style = MaterialTheme.typography.bodyMedium)
                            Text("Moderate",      style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFDAC1), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Column {
                            Text("Social needs", style = MaterialTheme.typography.bodyMedium)
                            Text("High",         style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFE4A0), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Column {
                            Text("Keep calm",    style = MaterialTheme.typography.bodyMedium)
                            Text("Yes",          style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("About", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "The Ragdoll is a placid cat but does not really go limp when you hold her. She is often composed and gets along well with all family members. Changes in routine generally do not upset her. She is an ideal companion.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

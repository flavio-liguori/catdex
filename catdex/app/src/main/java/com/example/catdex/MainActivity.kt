package com.example.catdex
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
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
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.health.connect.datatypes.ExerciseRoute
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.room.vo.Warning
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.catdex.ui.theme.CatdexTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import hexagonShape
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.random.Random

// --- modèle et données
data class CatBreed(
    val name: String,
    val imageName: String,  // Exemple : "siamois" si le fichier est res/drawable/siamois.png
    val pawCount: Int,
    val catCount: Int
)
var countchat = 0

val sampleBreeds = listOf(
    CatBreed("Abyssinian",      "abyssian",  pawCount = 2, catCount = 1),
    CatBreed("Sphinx","sphinx",  pawCount = 2, catCount = 1),
    CatBreed("Bombay","bombay",pawCount = 2, catCount = 1),
    CatBreed("Birman","birman",pawCount = 0, catCount = 3),
    CatBreed("Bengal",           "bengal",pawCount = 2, catCount = 2),
    CatBreed("British Shorthair","british",pawCount = 2, catCount = 1),
    CatBreed("russian blue",      "russian",pawCount = 2, catCount = 1),
    CatBreed("Egyptian Mau",     "egyptian",pawCount = 1, catCount = 2),
    CatBreed("Maine Coon",        "mainecoon",pawCount = 2, catCount = 1),
    CatBreed("Siamese",           "siamese",pawCount = 2, catCount = 1),
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
                count = 7,  // 0=Camera,1=Home,2=User,3=CatDex
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> CameraPage()
                    1 -> HomePage(
                        onCameraClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                        onBreedsClick = { scope.launch { pagerState.animateScrollToPage(3) } },
                        onProfileClick = { scope.launch { pagerState.animateScrollToPage(5) } },
                        onPhotoGalleryClick = { scope.launch { pagerState.animateScrollToPage(6) } }// ← ici
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
                    5 -> ProfileCatPage()
                    6 -> GalleryPage()
                }
            }
        }

        // 2) Barre de navigation persistante
        BottomNavBar(
            onCameraClick = { scope.launch { pagerState.animateScrollToPage(0) } },
            onHomeClick = { scope.launch { pagerState.animateScrollToPage(1) } },
            onUserClick = { scope.launch { pagerState.animateScrollToPage(2) } },
            onBreedsClick = { scope.launch { pagerState.animateScrollToPage(3) } }
        )

    }
}

// Barre basse utilisée globalement
@Composable
fun BottomNavBar(
    onCameraClick: () -> Unit,
    onHomeClick: () -> Unit,
    onUserClick: () -> Unit,
    onBreedsClick: () -> Unit
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
fun getPhotoCount(context: Context): Int {
   return countchat
}

@Composable
fun HomePage(
    onCameraClick: () -> Unit,
    onBreedsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onPhotoGalleryClick: () -> Unit,
    catsCount: Int = 0 // Nouveau paramètre pour le nombre de chats
) {
    // Palette moderne et minimaliste
    val backgroundColor = Color(0xFFFAFAFA)
    val surfaceColor = Color.White
    val primaryColor = Color(0xFF1A1A1A)
    val accentColor = Color(0xFF6366F1)  // Indigo moderne
    val secondaryColor = Color(0xFF64748B)
    val cardColor = Color(0xFFF8FAFC)
    val successColor = Color(0xFF10B981)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            // --- HEADER MINIMALISTE ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CatDex",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryColor,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Gestion féline moderne",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = secondaryColor,
                        modifier = Modifier.offset(y = (-4).dp)
                    )
                }

                // Avatar moderne et subtil
                var isAvatarClicked by remember { mutableStateOf(false) }
                val avatarScale by animateFloatAsState(
                    targetValue = if (isAvatarClicked) 0.95f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessHigh
                    ),
                    label = "avatarScale"
                )

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .scale(avatarScale)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(accentColor.copy(alpha = 0.1f), accentColor.copy(alpha = 0.05f))
                            )
                        )
                        .clickable {
                            isAvatarClicked = !isAvatarClicked
                            onBreedsClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_cat_avatar),
                        contentDescription = "Avatar chat",
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(accentColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- COMPTEUR DE CHATS (NOUVEAU) ---
            val androidContext = LocalContext.current
            val catsCount = remember { getPhotoCount(androidContext) }





            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = successColor.copy(alpha = 0.08f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        successColor.copy(alpha = 0.2f),
                                        successColor.copy(alpha = 0.1f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cat_avatar),
                            contentDescription = null,
                            tint = successColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Animation du compteur
                        val animatedCount by animateIntAsState(
                            targetValue = catsCount,
                            animationSpec = tween(
                                durationMillis = 1000,
                                easing = FastOutSlowInEasing
                            ),
                            label = "countAnimation"
                        )

                        Text(
                            text = "$animatedCount",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = successColor,
                            letterSpacing = (-1).sp
                        )
                        Text(
                            text = if (catsCount <= 1) "chat découvert" else "chats découverts",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = successColor.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    // Badge de progression
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(successColor.copy(alpha = 0.15f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = when {
                                catsCount == 0 -> "Débutant"
                                catsCount < 5 -> "Explorateur"
                                catsCount < 10 -> "Collecteur"
                                catsCount < 20 -> "Expert"
                                else -> "Maître CatDex"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = successColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- SECTION PRINCIPALE AVEC CARDS MODERNES ---
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Card Profil du chat
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onBreedsClick() },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(accentColor, accentColor.copy(alpha = 0.8f))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_cat_avatar),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Recherche des especes",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = primaryColor
                                )
                                Text(
                                    text = "Consultez les différentes espèces de chats",
                                    fontSize = 14.sp,
                                    color = secondaryColor,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = secondaryColor.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Card Mes chats (mise à jour avec le compteur)
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPhotoGalleryClick() },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_cat_avatar),
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Mes chats",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = primaryColor
                                    )
                                    if (catsCount > 0) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(accentColor)
                                                .size(20.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$catsCount",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = if (catsCount > 0) "Collection et historique ($catsCount)" else "Collection et historique",
                                    fontSize = 14.sp,
                                    color = secondaryColor,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = secondaryColor.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Card Appareil photo (action rapide)
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCameraClick() },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(successColor.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Camera,
                                    contentDescription = null,
                                    tint = successColor,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Scanner un chat",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = primaryColor
                                )
                                Text(
                                    text = "Identification par photo",
                                    fontSize = 14.sp,
                                    color = secondaryColor,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = secondaryColor.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Spacer pour éviter que le contenu colle au bas
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // --- DÉCONNEXION EN BAS ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            TextButton(
                onClick = { /* action déconnexion */ },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = secondaryColor
                )
            ) {
                Text(
                    text = "Déconnexion",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
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
    // Contexte pour récupérer dynamiquement l'ID du drawable
    val context = LocalContext.current

    // Récupère le resourceId associé au nom (imageName)
    val imageResId = remember(breed.imageName) {
        context.resources.getIdentifier(
            breed.imageName,    // doit correspondre exactement au nom du fichier dans res/drawable
            "drawable",
            context.packageName
        )
    }

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
            if (imageResId != 0) {
                // Si on a bien trouvé un drawable sous ce nom, on l'affiche
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "${breed.name} photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFFE0E0E0), CircleShape)
                )
            } else {
                // Sinon, on peut afficher un placeholder ou une icône par défaut
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF0F0F0))
                        .border(2.dp, Color(0xFFE0E0E0), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Image non trouvée",
                        tint = Color(0xFFB0B0B0),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = breed.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
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
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun CameraPage() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // État pour gérer les différentes fonctionnalités de la caméra
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    // Nouveau: état pour la géolocalisation
    var currentLocation: android.location.Location? by remember { mutableStateOf(null) }
    // Nouveau: état pour afficher/masquer la carte
    var showMap by remember { mutableStateOf(false) }

    // 1) Gestion des permissions caméra, stockage ET localisation
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    var hasStoragePermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                true
            } else {
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED
            }
        )
    }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    val storageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasStoragePermission = granted }
    )

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasLocationPermission = granted }
    )

    // Demander les permissions au lancement
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) cameraLauncher.launch(Manifest.permission.CAMERA)
        if (!hasStoragePermission && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            storageLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!hasLocationPermission) {
            locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Nouveau: LocationManager pour obtenir la position actuelle
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // Fonction pour obtenir la localisation actuelle
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(callback: (android.location.Location?) -> Unit) {
        if (!hasLocationPermission) {
            callback(null)
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    currentLocation = location
                    callback(location)
                    Log.d("Location", "Position obtenue: ${location.latitude}, ${location.longitude}")
                } else {
                    Log.d("Location", "Position non disponible")
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Location", "Erreur obtention position: ${e.message}")
                callback(null)
            }
    }

    // Fonction pour créer le dossier de sauvegarde
    fun getPhotoDirectory(): File {
        val mediaDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "SylvesterAI")
        } else {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SylvesterAI")
        }

        if (!mediaDir.exists()) {
            mediaDir.mkdirs()
        }
        return mediaDir
    }

    // Fonction pour obtenir le dossier de base de données locale
    fun getDatabaseDirectory(): File {
        val dbDir = File(context.filesDir, "photo_database")
        if (!dbDir.exists()) {
            dbDir.mkdirs()
        }
        return dbDir
    }
    // Fonction pour récupérer le nombre de photos
    fun getPhotoCount(): Int {
        val dir = getPhotoDirectory()
        // Filtre pour ne compter que les fichiers (pas les sous-dossiers) :
        return dir.listFiles()?.count { it.isFile } ?: 0
    }


    // Fonction pour mettre à jour l'index des photos
    fun updatePhotoIndex(photoId: String, photoData: JSONObject) {
        try {
            val indexFile = File(getDatabaseDirectory(), "photos_index.json")

            val photosArray = if (indexFile.exists()) {
                JSONArray(indexFile.readText())
            } else {
                JSONArray()
            }

            photosArray.put(photoData)
            indexFile.writeText(photosArray.toString())

            Log.d("LocalSave", "Index mis à jour avec ${photosArray.length()} photos")

        } catch (exception: Exception) {
            Log.e("LocalSave", "Erreur mise à jour index: ${exception.message}", exception)
        }
    }

    // Fonction pour sauvegarder les pins de chat (seulement pour les photos true)
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun saveCatPin(location: android.location.Location, photoId: String, photoFileName: String) {
        try {
            val catPinsFile = File(getDatabaseDirectory(), "cat_pins.json")

            val pinsArray = if (catPinsFile.exists()) {
                JSONArray(catPinsFile.readText())
            } else {
                JSONArray()
            }

            val pinData = JSONObject().apply {
                put("photoId", photoId)
                put("photoFileName", photoFileName)
                put("latitude", location.latitude)
                put("longitude", location.longitude)
                put("timestamp", System.currentTimeMillis())
                put("captureDate", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
            }

            pinsArray.put(pinData)
            catPinsFile.writeText(pinsArray.toString())

            Log.d("CatPins", "Pin de chat sauvegardé: ${location.latitude}, ${location.longitude}")

        } catch (exception: Exception) {
            Log.e("CatPins", "Erreur sauvegarde pin: ${exception.message}", exception)
        }
    }

    // Fonction pour récupérer tous les pins de chat
    fun getAllCatPins(): List<JSONObject> {
        return try {
            val catPinsFile = File(getDatabaseDirectory(), "cat_pins.json")
            if (catPinsFile.exists()) {
                val pinsArray = JSONArray(catPinsFile.readText())
                (0 until pinsArray.length()).map { pinsArray.getJSONObject(it) }
            } else {
                emptyList()
            }
        } catch (exception: Exception) {
            Log.e("CatPins", "Erreur lecture pins: ${exception.message}", exception)
            emptyList()
        }
    }

    // Fonction pour sauvegarder les métadonnées localement (modifiée pour inclure la localisation)
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun savePhotoMetadata(photoFile: File, isClassifiedTrue: Boolean, location: android.location.Location?) {
        isSaving = true

        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val photoId = UUID.randomUUID().toString()

            // Créer un objet JSON avec les métadonnées incluant la localisation
            val photoData = JSONObject().apply {
                put("id", photoId)
                put("fileName", photoFile.name)
                put("filePath", photoFile.absolutePath)
                put("timestamp", System.currentTimeMillis())
                put("isClassifiedTrue", isClassifiedTrue)
                put("fileSize", photoFile.length())
                put("captureDate", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))

                // Ajouter les informations de localisation
                if (location != null) {
                    put("latitude", location.latitude)
                    put("longitude", location.longitude)
                    put("hasLocation", true)
                } else {
                    put("hasLocation", false)
                }
            }

            // Sauvegarder dans un fichier JSON
            val metadataFile = File(getDatabaseDirectory(), "${photoId}.json")
            metadataFile.writeText(photoData.toString())

            // Créer/mettre à jour l'index des photos
            updatePhotoIndex(photoId, photoData)

            // Si la photo est classifiée comme "true" et qu'on a une localisation, sauvegarder le pin
            if (isClassifiedTrue && location != null) {
                saveCatPin(location, photoId, photoFile.name)
                Toast.makeText(context, "📍 Pin de chat ajouté à la carte!", Toast.LENGTH_SHORT).show()
            }

            isSaving = false

            val locationText = if (location != null) {
                "\n📍 Position: ${String.format("%.4f", location.latitude)}, ${String.format("%.4f", location.longitude)}"
            } else {
                "\n📍 Position non disponible"
            }

            val successMsg = "✅ Photo sauvegardée localement!\nClassification: $isClassifiedTrue$locationText"
            Toast.makeText(context, successMsg, Toast.LENGTH_LONG).show()
            Log.d("LocalSave", "✅ Photo et métadonnées sauvegardées: ${photoFile.name}")

        } catch (exception: Exception) {
            isSaving = false
            val errorMsg = "❌ Erreur sauvegarde locale: ${exception.message}"
            Log.e("LocalSave", errorMsg, exception)
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
        }
    }

    // Fonction pour capturer une photo - modifiée pour inclure la géolocalisation
    fun capturePhoto() {
        val imageCapture = imageCapture ?: run {
            Toast.makeText(context, "Caméra non initialisée", Toast.LENGTH_SHORT).show()
            return
        }

        // Créer un nom de fichier unique
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val randomId = UUID.randomUUID().toString().substring(0, 8)
        val photoFile = File(getPhotoDirectory(), "IMG_${timestamp}_${randomId}.jpg")

        Log.d("Capture", "Capture vers: ${photoFile.absolutePath}")

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        isCapturing = true

        // Obtenir la localisation avant de prendre la photo
        getCurrentLocation { location ->
            imageCapture.takePicture(
                outputFileOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exception: ImageCaptureException) {
                        isCapturing = false
                        val errorMsg = "❌ Erreur capture: ${exception.message}"
                        Log.e("Capture", errorMsg, exception)
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        isCapturing = false

                        Log.d("Capture", "✅ Photo capturée: ${photoFile.absolutePath}")
                        Log.d("Capture", "Taille fichier: ${photoFile.length()} bytes")

                        // Vérifier que le fichier a bien été créé
                        if (!photoFile.exists() || photoFile.length() == 0L) {
                            Toast.makeText(context, "❌ Erreur: fichier photo invalide", Toast.LENGTH_SHORT).show()
                            return
                        }

                        // Générer une classification aléatoire (true/false)
                        val isClassifiedTrue = Random.nextBoolean()

                        val classificationMsg = if (isClassifiedTrue) "🐱 Chat détecté!" else "❌ Pas de chat"
                        Toast.makeText(context, "✅ Photo capturée! $classificationMsg", Toast.LENGTH_SHORT).show()

                        // Ajouter la photo à la galerie (pour Android < 10)
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            MediaScannerConnection.scanFile(
                                context,
                                arrayOf(photoFile.absolutePath),
                                arrayOf("image/jpeg"),
                                { path, uri ->
                                    Log.d("MediaScanner", "Fichier ajouté à la galerie: $path")
                                }
                            )
                        }

                        // Sauvegarder avec les métadonnées incluant la localisation
                        Log.d("Capture", "Sauvegarde locale des métadonnées...")
                        savePhotoMetadata(photoFile, isClassifiedTrue, location)
                    }
                }
            )
        }
    }

    // Interface principale
    if (showMap) {
        // Afficher la carte
        CatMapView(
            catPins = getAllCatPins(),
            onBackPressed = { showMap = false }
        )
    } else {
        // Afficher l'interface caméra
        Box(modifier = Modifier.fillMaxSize()) {
            // Interface caméra existante
            if (hasCameraPermission && hasStoragePermission) {
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

                            imageCapture = ImageCapture.Builder()
                                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                                .setJpegQuality(95)
                                .build()

                            val selector = CameraSelector.DEFAULT_BACK_CAMERA

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    selector,
                                    preview,
                                    imageCapture
                                )
                                Log.d("Camera", "✅ Caméra initialisée avec succès")
                            } catch (exc: Exception) {
                                Log.e("Camera", "❌ Erreur initialisation caméra", exc)
                                Toast.makeText(ctx, "Erreur lors de l'initialisation de la caméra: ${exc.message}", Toast.LENGTH_LONG).show()
                            }
                        }, ContextCompat.getMainExecutor(ctx))

                        previewView
                    }
                )
            } else {
                // Permissions manquantes
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Autorisations requises:", color = Color.White)
                        if (!hasCameraPermission) Text("• Caméra", color = Color.White)
                        if (!hasStoragePermission) Text("• Stockage", color = Color.White)
                        if (!hasLocationPermission) Text("• Localisation", color = Color.White)
                    }
                }
            }

            // Cadre vert + overlay chat
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.Center)
                    .border(4.dp, Color(0xFF4CAF50), RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(128.dp)
                )
            }

            // Conseil et bouton prise de vue
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when {
                        isSaving -> "Sauvegarde locale en cours..."
                        isCapturing -> "Capture en cours..."
                        else -> "Prenez une photo de chat pour ajouter un pin sur la carte!"
                    },
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Bouton de capture
                IconButton(
                    onClick = {
                        if (!isCapturing && !isSaving && hasCameraPermission && hasStoragePermission) {
                            capturePhoto()
                        }
                    },
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            when {
                                isCapturing || isSaving -> Color.Gray
                                else -> Color.White
                            },
                            CircleShape
                        ),
                    enabled = !isCapturing && !isSaving && hasCameraPermission && hasStoragePermission
                ) {
                    when {
                        isCapturing -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                color = Color.DarkGray
                            )
                        }
                        isSaving -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                color = Color.Green
                            )
                        }
                        else -> {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Prendre photo",
                                tint = Color.DarkGray,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }
            }

            // Footer avec bouton carte
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("sylvester.ai", color = Color.White)

                Row {
                    // Nouveau bouton pour afficher la carte
                    IconButton(onClick = { showMap = true }) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Voir la carte",
                            tint = Color.White
                        )
                    }

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
    }
}
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun UserPage() {
    val context = LocalContext.current

    // Firebase instances
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userName = remember { currentUser?.displayName ?: "Utilisateur" }

    // Palette de couleurs ultra moderne avec gradients
    val backgroundColor = Color(0xFFF8FAFC)
    val surfaceColor = Color.White
    val primaryColor = Color(0xFF0F172A)
    val accentColor = Color(0xFF8B5CF6)
    val secondaryAccent = Color(0xFF06B6D4)
    val secondaryColor = Color(0xFF64748B)
    val successColor = Color(0xFF059669)
    val warningColor = Color(0xFFF59E0B)
    val errorColor = Color(0xFFDC2626)

    // État des tâches avec sauvegarde Firebase
    var tasks by remember { mutableStateOf<List<TaskItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fonction pour sauvegarder les tâches dans Firestore
    fun saveTasks(taskList: List<TaskItem>) {
        currentUser?.let { user ->
            val userTasksRef = firestore.collection("users").document(user.uid).collection("tasks")

            taskList.forEachIndexed { index, task ->
                userTasksRef.document("task_$index").set(
                    mapOf(
                        "label" to task.label,
                        "checked" to task.checked,
                        "timestamp" to FieldValue.serverTimestamp()
                    )
                ).addOnFailureListener { e ->
                    android.util.Log.e("UserPage", "Erreur lors de la sauvegarde: ${e.message}")
                }
            }
        }
    }

    // Fonction pour charger les tâches depuis Firestore
    fun loadTasks() {
        currentUser?.let { user ->
            val userTasksRef = firestore.collection("users").document(user.uid).collection("tasks")

            userTasksRef.orderBy("timestamp").get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        // Première connexion - créer les tâches par défaut
                        val defaultTasks = listOf(
                            TaskItem("Identifier 5 races de chats", false),
                            TaskItem("Parcourir 1 km avec mon chat", false),
                            TaskItem("Nettoyer la litière", false),
                            TaskItem("Jouer 15 minutes avec mon chat", false),
                            TaskItem("Donner des croquettes premium", false)
                        )
                        tasks = defaultTasks
                        saveTasks(defaultTasks)
                    } else {
                        // Charger les tâches existantes
                        val loadedTasks = documents.mapNotNull { document ->
                            try {
                                TaskItem(
                                    label = document.getString("label") ?: "",
                                    checked = document.getBoolean("checked") ?: false
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        tasks = loadedTasks
                    }
                    isLoading = false
                }
                .addOnFailureListener { e ->
                    Log.e("UserPage", "Erreur lors du chargement: ${e.message}")
                    // En cas d'erreur, utiliser les tâches par défaut
                    tasks = listOf(
                        TaskItem("Identifier 5 races de chats", false),
                        TaskItem("Parcourir 1 km avec mon chat", false),
                        TaskItem("Nettoyer la litière", false),
                        TaskItem("Jouer 15 minutes avec mon chat", false),
                        TaskItem("Donner des croquettes premium", false)
                    )
                    isLoading = false
                }
        }
    }

    // Charger les tâches au démarrage
    LaunchedEffect(Unit) {
        loadTasks()

    }

    val completedTasks = tasks.count { it.checked }
    val totalTasks = tasks.size
    val progressPercentage = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f

    // Affichage de chargement
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = accentColor,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Chargement de vos tâches...",
                    color = secondaryColor,
                    fontSize = 16.sp
                )
            }
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(backgroundColor, Color(0xFFF1F5F9))
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // --- HEADER PROFIL ULTRA MODERNE ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 12.dp
                    )
                ) {
                    Box {
                        // Effet de gradient en arrière-plan
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            accentColor.copy(alpha = 0.1f),
                                            secondaryAccent.copy(alpha = 0.05f),
                                            Color.Transparent
                                        ),
                                        radius = 300f
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Avatar avec animation et ombre
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .shadow(
                                        elevation = 12.dp,
                                        shape = CircleShape,
                                        ambientColor = accentColor.copy(alpha = 0.3f),
                                        spotColor = accentColor.copy(alpha = 0.3f)
                                    )
                                    .clip(CircleShape)
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(accentColor, secondaryAccent),
                                            start = Offset(0f, 0f),
                                            end = Offset(100f, 100f)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Avatar utilisateur",
                                    modifier = Modifier.size(60.dp),
                                    tint = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = userName,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = primaryColor
                            )

                            Text(
                                text = "✨ Explorateur félin passionné",
                                fontSize = 16.sp,
                                color = secondaryColor,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            // Barre de progression redesignée
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Progression quotidienne",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = primaryColor
                                    )

                                    Text(
                                        text = "${(progressPercentage * 100).toInt()}%",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (progressPercentage == 1f) successColor else accentColor
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFE2E8F0))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(progressPercentage)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    colors = if (progressPercentage == 1f) {
                                                        listOf(successColor, Color(0xFF34D399))
                                                    } else {
                                                        listOf(accentColor, secondaryAccent)
                                                    }
                                                )
                                            )
                                            .animateContentSize(
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessLow
                                                )
                                            )
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "$completedTasks sur $totalTasks tâches accomplies",
                                    fontSize = 14.sp,
                                    color = secondaryColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // --- SECTION MÉDAILLES PREMIUM ---
            item {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(accentColor, secondaryAccent)
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "🏆 Achievements",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val medals = listOf(
                            Triple("🥇 Or", warningColor, R.drawable.medal_gold),
                            Triple("🥈 Argent", Color(0xFF94A3B8), R.drawable.medal_silver),
                            Triple("🥉 Bronze", Color(0xFF92400E), R.drawable.medal_bronze),
                            Triple("💎 Platine", accentColor, R.drawable.medal_gold)
                        )

                        items(medals) { (name, color, drawable) ->
                            Card(
                                modifier = Modifier.width(140.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .shadow(
                                                elevation = 8.dp,
                                                shape = CircleShape,
                                                ambientColor = color.copy(alpha = 0.3f)
                                            )
                                            .clip(CircleShape)
                                            .background(
                                                Brush.radialGradient(
                                                    colors = listOf(
                                                        color.copy(alpha = 0.2f),
                                                        color.copy(alpha = 0.1f)
                                                    )
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = drawable),
                                            contentDescription = "Médaille $name",
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = color,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- SECTION TÂCHES INTERACTIVE ---
            item {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(successColor, Color(0xFF34D399))
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "📋 Missions quotidiennes",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            tasks.forEachIndexed { index, taskItem ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            if (taskItem.checked) {
                                                Brush.horizontalGradient(
                                                    colors = listOf(
                                                        successColor.copy(alpha = 0.1f),
                                                        successColor.copy(alpha = 0.05f)
                                                    )
                                                )
                                            } else {
                                                Brush.horizontalGradient(
                                                    colors = listOf(Color.Transparent, Color.Transparent)
                                                )
                                            }
                                        )
                                        .clickable {
                                            val updatedTasks = tasks.mapIndexed { i, task ->
                                                if (i == index) task.copy(checked = !task.checked) else task
                                            }
                                            tasks = updatedTasks
                                            saveTasks(updatedTasks) // Sauvegarder immédiatement
                                        }
                                        .padding(16.dp)
                                ) {
                                    // Checkbox moderne avec animation
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .shadow(
                                                elevation = if (taskItem.checked) 6.dp else 2.dp,
                                                shape = RoundedCornerShape(8.dp),
                                                ambientColor = if (taskItem.checked) successColor.copy(alpha = 0.3f) else Color.Gray.copy(alpha = 0.1f)
                                            )
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (taskItem.checked) {
                                                    Brush.linearGradient(
                                                        colors = listOf(successColor, Color(0xFF059669))
                                                    )
                                                } else {
                                                    Brush.linearGradient(
                                                        colors = listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0))
                                                    )
                                                }
                                            )
                                            .animateContentSize(),
                                        contentAlignment = Alignment.Center
                                    ) {

                                    }

                                    Spacer(modifier = Modifier.width(20.dp))

                                    Text(
                                        text = taskItem.label,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (taskItem.checked) secondaryColor else primaryColor,
                                        textDecoration = if (taskItem.checked) TextDecoration.LineThrough else null,
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (taskItem.checked) {
                                        Text(
                                            text = "✨",
                                            fontSize = 20.sp,
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }
                                }
                            }

                            // Message de félicitations si toutes les tâches sont complétées
                            AnimatedVisibility(
                                visible = progressPercentage == 1f,
                                enter = slideInVertically() + fadeIn(),
                                exit = slideOutVertically() + fadeOut()
                            ) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = successColor.copy(alpha = 0.1f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(20.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "🎉",
                                            fontSize = 32.sp
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(
                                                text = "Félicitations !",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = successColor
                                            )
                                            Text(
                                                text = "Toutes vos missions sont accomplies !",
                                                fontSize = 14.sp,
                                                color = secondaryColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- ESPACE POUR LE BOUTON DÉCONNEXION ---
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // --- BOUTON DÉCONNEXION PREMIUM ---
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
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
                    .padding(20.dp)
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(errorColor, Color(0xFFEF4444))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Se déconnecter",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
// Classe de support pour modéliser une tâche avec état coché/non-coché
private data class TaskItem(val label: String, var checked: Boolean = false)

@Composable
fun ProfileCatPage(
    onHexagonClick: () -> Unit = {},
    onBackgroundClick: () -> Unit = {},
    onBannerClick: () -> Unit = {},
    onVitrineClick: () -> Unit = {}
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF5EF))    // fond crème clair
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ─── 1) Hexagone contenant l’image pixel du chat ──────────────────────────────
        val hexSize: Dp = 160.dp

        Box(
            modifier = Modifier
                .size(hexSize)
                // Rendre l’hexagone cliquable
                .clickable { onHexagonClick() }
                // Bordure sombre de 4dp autour de l’hexagone
                .border(
                    width = 4.dp,
                    color = Color(0xFF333333),
                    shape = hexagonShape()
                )
                // Fond blanc à l’intérieur de l’hexagone
                .background(color = Color.White, shape = hexagonShape())
                // Découpe l’intérieur en hexagone
                .clip(hexagonShape()),
            contentAlignment = Alignment.Center
        ) {
            // Utilisez votre propre ressource "pixel_cat.png" dans res/drawable/
            AsyncImage(
                model = R.drawable.cat, // remplacez par votre image pixel cat
                contentDescription = "Chat Pixel",
                modifier = Modifier
                    .size(100.dp)   // ajustez si nécessaire
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ─── 2) Section “Background” ─────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                // Rendre cliquable
                .clickable { onBackgroundClick() }
                .background(
                    color = Color(0xFF81C784),              // vert pixel-style
                    shape = RoundedCornerShape(4.dp)
                )
                .border(
                    width = 2.dp,
                    color = Color(0xFF333333),
                    shape = RoundedCornerShape(4.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Background",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ─── 3) Section “Bannière” ────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                // Rendre cliquable
                .clickable { onBannerClick() }
                .background(
                    color = Color(0xFFFBC02D),              // doré clair
                    shape = RoundedCornerShape(4.dp)
                )
                .border(
                    width = 2.dp,
                    color = Color(0xFF333333),
                    shape = RoundedCornerShape(4.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Bannière",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF333333)  // texte sombre
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ─── 4) Section “Vitrine” ────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                // Rendre cliquable
                .clickable { onVitrineClick() }
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(4.dp)
                )
                .border(
                    width = 2.dp,
                    color = Color(0xFF333333),
                    shape = RoundedCornerShape(4.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Vitrine",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFFF57C00)  // corail/orangé
            )
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
    val context = LocalContext.current
    var isLiked by remember { mutableStateOf(false) }

    // Récupère dynamiquement l'ID du drawable à partir du nom (imageName)
    val imageResId = remember(breed.imageName) {
        context.resources.getIdentifier(
            breed.imageName,
            "drawable",
            context.packageName
        )
    }

    // Couleurs modernes
    val primaryColor = Color(0xFF1A1A1A)
    val secondaryColor = Color(0xFF64748B)
    val surfaceColor = Color(0xFFFEFEFE)
    val accentColor = Color(0xFF6366F1)
    val cardColors = listOf(
        Color(0xFFF1F5F9),  // Gris clair
        Color(0xFFFEF3C7),  // Jaune pâle
        Color(0xFFECFDF5)   // Vert pâle
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor)
    ) {
        // --- IMAGE HERO AVEC OVERLAY GRADIENT ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            if (imageResId != 0) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "${breed.name} image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient overlay pour améliorer la lisibilité
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f)
                                ),
                                startY = 0f,
                                endY = 400f
                            )
                        )
                )
            } else {
                // Placeholder moderne
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFF8FAFC),
                                    Color(0xFFE2E8F0)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = "Image non trouvée",
                            tint = secondaryColor.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Image non disponible",
                            color = secondaryColor.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // --- HEADER CONTROLS FLOATING ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Bouton retour moderne
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Retour",
                    tint = primaryColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Bouton favori moderne
            val likeScale by animateFloatAsState(
                targetValue = if (isLiked) 1.2f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh
                ),
                label = "likeScale"
            )

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .scale(likeScale)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f))
                    .clickable { isLiked = !isLiked },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favori",
                    tint = if (isLiked) Color(0xFFEF4444) else primaryColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // --- CONTENU PRINCIPAL AVEC BOTTOM SHEET STYLE ---
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 320.dp),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = surfaceColor,
            shadowElevation = 24.dp
        ) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 24.dp),
                contentPadding = PaddingValues(vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Titre et nom de la race
                item {
                    Column {
                        Text(
                            text = breed.name,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryColor,
                            letterSpacing = (-1).sp
                        )
                        Text(
                            text = "Race féline",
                            fontSize = 16.sp,
                            color = secondaryColor,
                            modifier = Modifier.offset(y = (-4).dp)
                        )
                    }
                }

                // Stats cards modernes
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val stats = listOf(
                            Triple("Compatibilité", "Modérée", 0),
                            Triple("Socialisation", "Élevée", 1),
                            Triple("Tempérament", "Calme", 2)
                        )

                        stats.forEach { (title, value, index) ->
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = cardColors[index]
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = value,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = primaryColor
                                    )
                                    Text(
                                        text = title,
                                        fontSize = 12.sp,
                                        color = secondaryColor,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Section À propos
                item {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(accentColor)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "À propos",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = primaryColor
                            )
                        }

                        Text(
                            text = "Le Ragdoll est un chat placide mais ne devient pas vraiment mou quand vous le tenez. Il est souvent composé et s'entend bien avec tous les membres de la famille. Les changements de routine ne le dérangent généralement pas. C'est un compagnon idéal pour la vie de famille moderne.",
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            color = secondaryColor,
                            modifier = Modifier.padding(start = 18.dp)
                        )
                    }
                }

                // Caractéristiques détaillées
                item {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(accentColor)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Caractéristiques",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = primaryColor
                            )
                        }

                        val characteristics = listOf(
                            "Affectueux" to "★★★★★",
                            "Actif" to "★★★☆☆",
                            "Bavard" to "★★☆☆☆",
                            "Intelligence" to "★★★★☆"
                        )

                        characteristics.forEach { (trait, rating) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 18.dp, bottom = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = trait,
                                    fontSize = 16.sp,
                                    color = secondaryColor
                                )
                                Text(
                                    text = rating,
                                    fontSize = 16.sp,
                                    color = Color(0xFFFBBF24),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Espace pour éviter que le contenu touche le bas
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}



// Data class pour les photos
data class PhotoData(
    val id: String = "",
    val fileName: String = "",
    val filePath: String = "",
    val timestamp: Long = 0L,
    val isClassifiedTrue: Boolean = false,
    val fileSize: Long = 0L,
    val captureDate: String = ""
)

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun GalleryPage() {
    val context = LocalContext.current
    var photos by remember { mutableStateOf<List<PhotoData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fonction pour obtenir le dossier de base de données locale
    fun getDatabaseDirectory(): File {
        val dbDir = File(context.filesDir, "photo_database")
        if (!dbDir.exists()) {
            dbDir.mkdirs()
        }
        return dbDir
    }

    // Fonction pour charger toutes les photos depuis le stockage local
    fun loadLocalPhotos(): Int{
        try {
            val indexFile = File(getDatabaseDirectory(), "photos_index.json")

            if (!indexFile.exists()) {
                photos = emptyList()
                isLoading = false
                return 0
            }

            val photosArray = JSONArray(indexFile.readText())
            val photoList = mutableListOf<PhotoData>()

            for (i in 0 until photosArray.length()) {
                try {
                    val photoJson = photosArray.getJSONObject(i)

                    // Créer l'objet PhotoData depuis le JSON
                    val photo = PhotoData(
                        id = photoJson.optString("id", ""),
                        fileName = photoJson.optString("fileName", ""),
                        filePath = photoJson.optString("filePath", ""),
                        timestamp = photoJson.optLong("timestamp", 0L),
                        isClassifiedTrue = photoJson.optBoolean("isClassifiedTrue", false),
                        fileSize = photoJson.optLong("fileSize", 0L),
                        captureDate = photoJson.optString("captureDate", "")
                    )

                    // Vérifier que le fichier photo existe toujours
                    val photoFile = File(photo.filePath)
                    if (photoFile.exists()) {
                        // Ajouter seulement les photos classifiées comme true
                        if (photo.isClassifiedTrue) {
                            photoList.add(photo)
                        }
                    } else {
                        Log.w("GalleryPage", "Fichier photo introuvable: ${photo.filePath}")
                    }

                } catch (e: Exception) {
                    Log.e("GalleryPage", "Erreur parsing photo JSON: ${e.message}")
                }
            }

            // Trier par timestamp décroissant (plus récent en premier)
            photos = photoList.sortedByDescending { it.timestamp }
            countchat = photoList.size
            isLoading = false
            errorMessage = null

            Log.d("GalleryPage", "✅ ${photos.size} photos chargées depuis le stockage local")
            return countchat
        } catch (e: Exception) {
            isLoading = false
            errorMessage = "Erreur de chargement local: ${e.message}"
            Log.e("GalleryPage", "Erreur chargement photos locales: ${e.message}")
        }
        return 0
    }

    // Charger les photos au démarrage
    LaunchedEffect(Unit) {
        countchat = loadLocalPhotos()

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Galerie Photos True",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        isLoading = true
                        loadLocalPhotos()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Actualiser",
                        tint = Color.White
                    )
                }

                Text(
                    text = "${photos.size} photos",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

        // Contenu principal
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Chargement des photos...", color = Color.White)
                    }
                }
            }

            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                isLoading = true
                                errorMessage = null
                                loadLocalPhotos()
                            }
                        ) {
                            Text("Réessayer")
                        }
                    }
                }
            }

            photos.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Aucune photo classifiée 'true' trouvée",
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Prenez des photos avec l'appareil photo !",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(photos) { photo ->
                        PhotoItem(photo = photo)
                    }
                }
            }
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoItem(photo: PhotoData) {
    var isImageLoading by remember { mutableStateOf(true) }
    var imageLoadError by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable {
                // TODO : ouvrir en plein écran ou afficher détails
                Log.d("PhotoItem", "Photo cliquée : ${photo.fileName}")
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                imageLoadError -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.BrokenImage,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Erreur de chargement",
                            color = Color.Gray,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                isImageLoading -> {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(File(photo.filePath))
                    .build(),
                contentDescription = "Photo ${photo.fileName}",
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                onLoading = { isImageLoading = true },
                onSuccess = {
                    isImageLoading = false
                    imageLoadError = false
                },
                onError = {
                    isImageLoading = false
                    imageLoadError = true
                    Log.e("PhotoItem", "Erreur chargement image : ${photo.filePath}")
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            .format(Date(photo.timestamp)),
                        color = Color.White,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "${(photo.fileSize / 1024).toInt()} KB",
                        color = Color.Gray,
                        fontSize = 8.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CatMapView(
    catPins: List<JSONObject>,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    onCreate(null)
                    onResume()
                    getMapAsync { map ->
                        googleMap = map
                        setupMap(map, catPins, ctx)
                    }
                }
            },
            update = { _ ->
                googleMap?.let { setupMap(it, catPins, context) }
            }
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                    Text(
                        text = "Carte des chats détectés",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Badge(
                    containerColor = Color(0xFF4CAF50)
                ) {
                    Text(
                        text = "${catPins.size}",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        if (catPins.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Statistiques",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatisticItem(
                            icon = Icons.Default.Pets,
                            label = "Chats détectés",
                            value = "${catPins.size}"
                        )

                        StatisticItem(
                            icon = Icons.Default.LocationOn,
                            label = "Lieux visités",
                            value = "${getUniqueLocationsCount(catPins)}"
                        )

                        StatisticItem(
                            icon = Icons.Default.Schedule,
                            label = "Dernière photo",
                            value = getLastPhotoTime(catPins)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun setupMap(map: GoogleMap, catPins: List<JSONObject>, context: Context) {
    try {
        map.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMyLocationButtonEnabled = true
            isMapToolbarEnabled = true
        }

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        }

        if (catPins.isEmpty()) {
            val defaultLocation = LatLng(48.8566, 2.3522)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
            return
        }

        val boundsBuilder = LatLngBounds.Builder()
        var hasValidLocation = false

        catPins.forEach { pin ->
            try {
                val latitude = pin.getDouble("latitude")
                val longitude = pin.getDouble("longitude")
                val photoFileName = pin.optString("photoFileName", "Inconnu")
                val captureDate = pin.optString("captureDate", "Inconnue")

                val position = LatLng(latitude, longitude)
                map.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title("🐱 Chat détecté")
                        .snippet("Photo : $photoFileName\nDate : $captureDate")
                        .icon(createCatMarkerIcon(context))
                )

                boundsBuilder.include(position)
                hasValidLocation = true

                Log.d("CatMap", "Marqueur ajouté : $latitude, $longitude - $photoFileName")
            } catch (e: Exception) {
                Log.e("CatMap", "Erreur ajout marqueur : ${e.message}")
            }
        }

        if (hasValidLocation) {
            try {
                val padding = 100
                val cameraUpdate = CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), padding)
                map.animateCamera(cameraUpdate)
            } catch (e: Exception) {
                Log.e("CatMap", "Erreur ajustement caméra : ${e.message}")
                catPins.firstOrNull()?.let {
                    val lat = it.getDouble("latitude")
                    val lng = it.getDouble("longitude")
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 15f))
                }
            }
        }

        map.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: com.google.android.gms.maps.model.Marker): View? = null
            override fun getInfoContents(marker: com.google.android.gms.maps.model.Marker): View? {
                val view = LayoutInflater.from(context).inflate(
                    android.R.layout.simple_list_item_2, null
                )
                val title = view.findViewById<TextView>(android.R.id.text1)
                val snippet = view.findViewById<TextView>(android.R.id.text2)
                title.text = marker.title
                snippet.text = marker.snippet
                return view
            }
        })

        Log.d("CatMap", "Carte configurée avec ${catPins.size} pins")
    } catch (e: Exception) {
        Log.e("CatMap", "Erreur configuration carte : ${e.message}", e)
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
fun createCatMarkerIcon(context: Context): BitmapDescriptor {
    return try {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = Color(0xFF4CAF50).toArgb()
            isAntiAlias = true
        }
        canvas.drawCircle(50f, 50f, 45f, paint)
        paint.color = Color.White.toArgb()
        canvas.drawCircle(50f, 50f, 35f, paint)
        paint.apply {
            color = Color(0xFF4CAF50).toArgb()
            textSize = 40f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("🐱", 50f, 65f, paint)
        BitmapDescriptorFactory.fromBitmap(bitmap)
    } catch (e: Exception) {
        Log.e("CatMap", "Erreur création icône : ${e.message}")
        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
fun getUniqueLocationsCount(catPins: List<JSONObject>): Int {
    val uniqueLocations = mutableSetOf<String>()
    catPins.forEach { pin ->
        try {
            val lat = pin.getDouble("latitude")
            val lng = pin.getDouble("longitude")
            val locationKey = "${"%.4f".format(lat)}-${"%.4f".format(lng)}"
            uniqueLocations.add(locationKey)
        } catch (e: Exception) {
            Log.e("CatMap", "Erreur comptage lieux : ${e.message}")
        }
    }
    return uniqueLocations.size
}

@androidx.annotation.OptIn(UnstableApi::class)
fun getLastPhotoTime(catPins: List<JSONObject>): String {
    if (catPins.isEmpty()) return "Aucune"
    return try {
        val lastPin = catPins.maxByOrNull { pin ->
            try {
                pin.getLong("timestamp")
            } catch (e: Exception) {
                0L
            }
        }
        lastPin?.let {
            val timestamp = it.getLong("timestamp")
            val now = System.currentTimeMillis()
            val diffMinutes = (now - timestamp) / (1000 * 60)
            when {
                diffMinutes < 1 -> "Maintenant"
                diffMinutes < 60 -> "${diffMinutes}m"
                diffMinutes < 1440 -> "${diffMinutes / 60}h"
                else -> "${diffMinutes / 1440}j"
            }
        } ?: "Inconnue"
    } catch (e: Exception) {
        Log.e("CatMap", "Erreur calcul dernière photo : ${e.message}")
        "Erreur"
    }
}


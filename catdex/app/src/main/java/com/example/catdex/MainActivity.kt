package com.example.catdex

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.format.DateFormat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.catdex.api.Breed
import com.example.catdex.api.TheCatApiService
import com.example.catdex.ui.theme.CatdexTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
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
fun CameraPage(onClose: () -> Unit = {}) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 1) Permission caméra
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasPermission = granted }
    )
    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // 2) CameraX : Preview + ImageCapture
    var imageCaptureUseCase by remember { mutableStateOf<ImageCapture?>(null) }
    var previewView: PreviewView? = null

    // 3) Stockage de l’image capturée pour déclencher l’upload
    var photoFile by remember { mutableStateOf<File?>(null) }

    // 4) Stockage du résultat (race) et état de chargement
    var detectedBreed by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    // 5) État du switch avant/arrière
    var lensFacing by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }

    // 6) Trigger d’upload dès que 'photoFile' change
    LaunchedEffect(photoFile) {
        val file = photoFile
        if (file != null) {
            isUploading = true
            val result = uploadToTheCatApiSuspend(file)
            detectedBreed = result
            // Supprimer le fichier temporaire après upload
            file.delete()
            photoFile = null   // reset pour pouvoir capturer une autre photo plus tard
            isUploading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasPermission) {
            // 7) Affichage du PreviewView de CameraX
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val pv = PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                    previewView = pv

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        // a) Use-case Preview
                        val preview = Preview.Builder().build().also { pre ->
                            pre.setSurfaceProvider(pv.surfaceProvider)
                        }

                        // b) Use-case ImageCapture
                        val ic = ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                            .build()
                        imageCaptureUseCase = ic

                        // c) Bind use-cases au cycle de vie
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            lensFacing,
                            preview,
                            ic
                        )
                    }, ContextCompat.getMainExecutor(ctx))

                    pv
                }
            )
        } else {
            // Si on n'a pas la permission caméra, on affiche un message
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Permission caméra requise",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // 8) Barre supérieure : bouton "Retour"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = { onClose() },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Retour",
                    tint = Color.White
                )
            }
        }

        // 9) Viewfinder circulaire au centre
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.Center)
                .border(4.dp, Color(0xFF4CAF50), CircleShape)
                .clip(CircleShape)
        )

        // 10) Afficher la race détectée si elle existe
        detectedBreed?.let { breedName ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
                    .background(Color(0xAA000000), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Race détectée : $breedName",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // 11) Barre inférieure : capture + switch caméra
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 11.a) Bouton pour changer de caméra (avant/arrière)
            IconButton(
                onClick = {
                    lensFacing = if (lensFacing == CameraSelector.DEFAULT_BACK_CAMERA) {
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    } else {
                        CameraSelector.DEFAULT_BACK_CAMERA
                    }
                    // Re-bind avec le nouveau lensFacing
                    previewView?.let { pv ->
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also { pre ->
                                pre.setSurfaceProvider(pv.surfaceProvider)
                            }
                            imageCaptureUseCase?.let { ic ->
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    lensFacing,
                                    preview,
                                    ic
                                )
                            }
                        }, ContextCompat.getMainExecutor(context))
                    }
                },
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.FlipCameraAndroid,
                    contentDescription = "Changer caméra",
                    tint = Color.White
                )
            }

            // 11.b) Bouton capture
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(4.dp, Color(0xFF4CAF50), CircleShape)
                    .clickable(enabled = !isUploading) {
                        val ic = imageCaptureUseCase
                        if (ic != null) {
                            // Création d’un fichier temporaire dans le cache
                            val filename = SimpleDateFormat(
                                "yyyyMMdd_HHmmss",
                                Locale.US
                            ).format(System.currentTimeMillis()) + ".jpg"
                            val file = File(context.cacheDir, filename)

                            val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
                            isUploading = true
                            ic.takePicture(
                                outputOptions,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                        // 12) Affecter le fichier pour déclencher le LaunchedEffect
                                        photoFile = file
                                    }
                                    override fun onError(exc: ImageCaptureException) {
                                        isUploading = false
                                        detectedBreed = "Erreur capture : ${exc.message}"
                                        file.delete()
                                    }
                                }
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isUploading) {
                    CircularProgressIndicator(
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.size(40.dp),
                        strokeWidth = 4.dp
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50))
                    )
                }
            }

            // 11.c) Spacer pour équilibrer la rangée
            Spacer(modifier = Modifier.width(56.dp))
        }
    }
}


// 13) Fonction suspendue qui fait l’upload à TheCatAPI et renvoie le nom de la race
private suspend fun uploadToTheCatApiSuspend(file: File): String {
    return withContext(Dispatchers.IO) {
        val mediaType = "image/jpeg".toMediaTypeOrNull()
        val requestBody = file.asRequestBody(mediaType)
        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)

        return@withContext try {
            val service = TheCatApiService.create()
            val response = service.uploadCatImage(part)
            if (response.isSuccessful) {
                val body = response.body()
                val breeds = body?.breeds
                if (!breeds.isNullOrEmpty()) {
                    breeds[0].name ?: "Race inconnue"
                } else {
                    "Aucune race détectée"
                }
            } else {
                "Erreur API : ${response.code()}"
            }
        } catch (e: Exception) {
            "Exception : ${e.localizedMessage}"
        }
    }
}

// 11) Fonction utilitaire suspendue pour uploader l’image à TheCatAPI
//     et extraire la race détectée. Appelle un callback avec le nom de la race ou un message d’erreur.
private suspend fun uploadToTheCatApi(
    file: File,
    onResult: (String?) -> Unit
) {
    // On crée un MultipartBody.Part à partir du fichier
    val mediaType = "image/jpeg".toMediaTypeOrNull()
    val requestBody = file.asRequestBody(mediaType)
    val part = MultipartBody.Part.createFormData("file", file.name, requestBody)

    try {
        val service = TheCatApiService.create()
        val response = service.uploadCatImage(part)
        if (response.isSuccessful) {
            val body = response.body()
            // TheCatAPI renvoie une liste "breeds". On prend le premier élément si présent.
            val breeds = body?.breeds
            if (breeds != null && breeds.isNotEmpty()) {
                onResult(breeds[0].name ?: "Race inconnue")
            } else {
                onResult("Aucune race détectée")
            }
        } else {
            onResult("Erreur API : ${response.code()}")
        }
    } catch (e: Exception) {
        onResult("Exception : ${e.localizedMessage}")
    }
}
@Composable
fun CatBreedResultScreen(viewModel: CatBreedViewModel) {
    val breedState by viewModel.breedResult.collectAsState()

    when (val result = breedState) {
        null -> {
            // Pas encore de requête lancée : soit on affiche un bouton "Take Photo" ou rien
            Text("Aucune image analysée pour l’instant")
        }
        is Result.Success -> {
            val breeds = result.getOrNull() ?: emptyList()
            if (breeds.isEmpty()) {
                Text("Aucune race détectée")
            } else {
                // Affiche la liste des races retournées (ou la première)
                Column {
                    breeds.forEach { breed ->
                        Text("Race détectée : ${breed.name}")
                        Text("Origine : ${breed.origin}")
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
        is Result.Failure -> {
            val exception = result.exceptionOrNull()
            Text("Erreur : ${exception?.message ?: "inconnue"}")
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

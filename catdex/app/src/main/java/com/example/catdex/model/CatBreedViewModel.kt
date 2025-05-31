package com.example.catdex

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catdex.api.Breed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

class CatBreedViewModel(
    private val context: Context
) : ViewModel() {

    // On crée l’instance de l’API en dur
    private val catApi = TheCatApi.create(
        "live_NJb2NVSXrpPMGU76D55FJcbhsJt0Iw5OR46aG4W2F4jAbvKoHqiVU9q3FOBMEgPY"
    )

    // État pour exposer le résultat (liste de races ou erreur)
    private val _breedResult = MutableStateFlow<Result<List<Breed>>?>(null)
    val breedResult: StateFlow<Result<List<Breed>>?> = _breedResult

    /** 1) Convertit un Uri en File temporaire dans le cache */
    private fun uriToFile(uri: Uri): File {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val fileName = returnCursor.getString(nameIndex)
        returnCursor.close()

        val tempFile = File(context.cacheDir, fileName)
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val outputStream = FileOutputStream(tempFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        return tempFile
    }

    /**
     * 2) Lance la séquence d’upload + récupération de la race.
     */
    fun detectCatBreed(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // --- Étape A : Créer un File temporaire
                val fileToUpload = uriToFile(uri)

                // --- Étape B : Construire MultipartBody.Part
                val mediaType = "image/jpeg".toMediaTypeOrNull()
                val requestFile = RequestBody.create(mediaType, fileToUpload)
                val multipartBody = MultipartBody.Part.createFormData(
                    "file",        // champ attendu côté serveur
                    fileToUpload.name,
                    requestFile
                )

                // --- Étape C : Appel uploadImage AVEC CLÉ EN DUR (paramètre `apiKey`)
                val uploadResponse = catApi.uploadImage(
                    apiKey = "live_NJb2NVSXrpPMGU76D55FJcbhsJt0Iw5OR46aG4W2F4jAbvKoHqiVU9q3FOBMEgPY",
                    file   = multipartBody
                )
                if (!uploadResponse.isSuccessful) {
                    val code = uploadResponse.code()
                    _breedResult.value = Result.failure(Exception("Upload failed: HTTP $code"))
                    return@launch
                }

                val uploadBody = uploadResponse.body()
                if (uploadBody == null || uploadBody.id?.isBlank() == true) {
                    _breedResult.value = Result.failure(Exception("Upload response invalid"))
                    return@launch
                }

                // --- Étape D : Appel getImageWithBreeds AVEC CLÉ EN DUR (paramètre `apiKey`)
                val imageId = uploadBody.id
                val getResponse = catApi.getImageWithBreeds(
                    apiKey  = "live_NJb2NVSXrpPMGU76D55FJcbhsJt0Iw5OR46aG4W2F4jAbvKoHqiVU9q3FOBMEgPY",
                    imageId = imageId.toString()
                )

                if (!getResponse.isSuccessful) {
                    val code = getResponse.code()
                    _breedResult.value = Result.failure(Exception("Get image failed: HTTP $code"))
                    return@launch
                }

                val imageWithBreeds = getResponse.body()
                if (imageWithBreeds == null) {
                    _breedResult.value = Result.failure(Exception("Empty response"))
                    return@launch
                }

                // --- Étape E : Interpréter le résultat
                val breeds = imageWithBreeds.breeds
                if (breeds.isEmpty()) {
                    // Aucune race détectée
                    _breedResult.value = Result.success(emptyList())
                } else {
                    // On renvoie la liste des races détectées
                    _breedResult.value = Result.success(breeds)
                }

            } catch (e: Exception) {
                // Toutes autres erreurs (IO, JSON, etc.)
                _breedResult.value = Result.failure(e)
            }
        }
    }
}

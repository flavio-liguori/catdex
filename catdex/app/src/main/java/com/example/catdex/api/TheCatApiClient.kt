// /app/src/main/java/com/example/catdex/api/TheCatApiClient.kt
package com.example.catdex.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// 1) Data class pour parser la réponse JSON de TheCatAPI
//    On se concentre sur l’objet top-level retourné par /v1/images/upload,
//    qui contient au moins un champ "id" (String) et un tableau "breeds" (liste de breeds détectées).
data class BreedInfo(
    val id: String?,
    val name: String?
)

data class UploadImageResponse(
    val id: String?,                  // identifiant de l’image côté TheCatAPI
    val url: String?,                 // URL publique de l’image uploadée
    val breeds: List<BreedInfo>?      // liste (vide ou à 1 élément) contenant la/des race(s) détectée(s)
)

// 2) Interface Retrofit pour TheCatAPI 
interface TheCatApiService {
    @Multipart
    @POST("images/upload")
    suspend fun uploadCatImage(
        @Part imageFile: MultipartBody.Part
    ): Response<UploadImageResponse>

    companion object {
        private const val BASE_URL = "https://api.thecatapi.com/v1/"
        private const val API_KEY = "live_NJb2NVSXrpPMGU76D55FJcbhsJt0Iw5OR46aG4W2F4jAbvKoHqiVU9q3FOBMEgPY"
        fun create(): TheCatApiService {
            // Logging HTTP (optionnel, à désactiver en prod)
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                // On ajoute l’API key dans chaque requête
                .addInterceptor { chain ->
                    val original = chain.request()
                    val requestWithApiKey = original.newBuilder()
                        .header("x-api-key", API_KEY)
                        .build()
                    chain.proceed(requestWithApiKey)
                }
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TheCatApiService::class.java)
        }
    }
}

import com.example.catdex.api.ImageWithBreedsResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import com.example.catdex.api.UploadImageResponse

interface TheCatApi {

    /**
     * 1) Téléverse une image en multipart/form-data.
     *    L’API requiert :
     *      - userfile : le fichier image (MultipartBody.Part)
     *    En-tête HTTP :
     *      - x-api-key : votre clé d’API (exclure tout \n ou espace)
     */
    @Multipart
    @POST("v1/images/upload")
    suspend fun uploadImage(
        @Header("x-api-key") apiKey: String,
        @Part file: MultipartBody.Part
    ): Response<UploadImageResponse>


    /**
     * 2) Récupère l’objet image avec inclusion des races (include_breeds=1).
     *    On passe l’ID retourné par uploadImage().
     */
    @GET("v1/images/{image_id}")
    suspend fun getImageWithBreeds(
        @Header("x-api-key") apiKey: String,
        @Path("image_id") imageId: String,
        @Query("include_breeds") includeBreeds: Int = 1 // 1 pour true
    ): Response<ImageWithBreedsResponse>


    companion object {
        fun create(apiKeyRaw: String): TheCatApi {
            // 1) On nettoie la clé : retire tout \n ou espace
            val apiKey = apiKeyRaw.trim()

            // 2) Logging interceptor (optionnel, mais utile en dev)
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor { chain ->
                    // Vérification supplémentaire si apiKey est vide
                    if (apiKey.isBlank()) {
                        throw IllegalArgumentException("API Key is blank or invalid")
                    }
                    // On passe simplement la requête au chain (pas besoin d’autre header ici)
                    chain.proceed(chain.request())
                }
                .build()

            return Retrofit.Builder()
                .baseUrl("https://api.thecatapi.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TheCatApi::class.java)
        }
    }
}

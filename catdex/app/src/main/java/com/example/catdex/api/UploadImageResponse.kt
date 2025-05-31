package com.example.catdex.api

data class UploadImageResponse(
    val id: String,
    val url: String,
    val original_filename: String? = null,
    val sub_id: String? = null,
    val breed_ids: String? = null,
    val created_at: String? = null
)
data class Breed(
    val id: String,
    val name: String,
    val temperament: String?,
    val life_span: String?,
    val origin: String?
    // ajoutez d’autres champs si nécessaire
)

data class ImageWithBreedsResponse(
    val breeds: List<Breed>,
    val id: String,
    val url: String,
    val width: Int,
    val height: Int
)

package com.m3u.feature.films.pocketbase.models

import com.google.gson.annotations.SerializedName

private const val BASE_URL = "https://ipxtv.tijorat.net/api/files/" // Base URL for file access

data class FilmsResponse(
    @SerializedName("items") val items: List<Film>
)

data class Film(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("image") val image: String, // Original filename
    @SerializedName("trailer_url") val trailerUrl: String,
    @SerializedName("film_url") val filmUrl: String,
    @SerializedName("featured") val featured: Boolean,
    @SerializedName("categories") val categories: List<String>,
    @SerializedName("collectionName") val collectionName: String // Added collection name
) {
    // Computed property to return the full image URL
    val imageUrl: String get() = "$BASE_URL$collectionName/$id/$image"
}

data class CategoriesResponse(
    @SerializedName("items") val items: List<Category>
)

data class Category(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String, // Added category description
    @SerializedName("image") val image: String, // Original filename
    @SerializedName("collectionName") val collectionName: String // Added collection name
) {
    // Computed property to return the full image URL
    val imageUrl: String get() = "$BASE_URL$collectionName/$id/$image"
}

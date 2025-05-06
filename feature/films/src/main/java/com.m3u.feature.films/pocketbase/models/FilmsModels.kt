package com.m3u.feature.films.pocketbase.models

import com.google.gson.annotations.SerializedName

private const val BASE_URL = "https://ipxtv.tijorat.net/api/files/"

data class FilmsResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("perPage") val perPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalItems") val totalItems: Int,
    @SerializedName("items") val items: List<Film>
)

data class Film(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("image") val image: String,
    @SerializedName("trailer_url") val trailerUrl: String,
    @SerializedName("film_url") val filmUrl: String,
    @SerializedName("featured") val featured: Boolean,
    @SerializedName("categories") val categories: List<String>,
    @SerializedName("collectionName") val collectionName: String,
    @SerializedName("created") val created: String,
    @SerializedName("updated") val updated: String
) {
    val imageUrl: String get() = "$BASE_URL$collectionName/$id/$image"
}

data class CategoriesResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("perPage") val perPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalItems") val totalItems: Int,
    @SerializedName("items") val items: List<Category>
)

data class Category(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("image") val image: String,
    @SerializedName("collectionName") val collectionName: String,
    @SerializedName("created") val created: String,
    @SerializedName("updated") val updated: String
) {
    val imageUrl: String get() = "$BASE_URL$collectionName/$id/$image"
}

data class CommentsResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("perPage") val perPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalItems") val totalItems: Int,
    @SerializedName("items") val items: List<Comment>
)

data class Comment(
    @SerializedName("id") val id: String,
    @SerializedName("text") val text: String,
    @SerializedName("film") val film: String,
    @SerializedName("device") val device: String?,
    @SerializedName("created") val created: String,
    @SerializedName("updated") val updated: String,
    @SerializedName("collectionName") val collectionName: String,
    @SerializedName("is_approved") val isApproved: Boolean
)
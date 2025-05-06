package com.m3u.feature.films.pocketbase

import com.m3u.feature.films.pocketbase.models.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Query

interface FilmsService {

    @GET("api/collections/films/records")
    suspend fun getFilms(
        @Query("filter") filter: String? = null,
        @Query("sort") sort: String? = null,
        @Query("page") page: Int = 1,
        @Query("perPage") perPage: Int = 30
    ): Response<FilmsResponse>

    @GET("api/collections/film_categories/records")
    suspend fun getCategories(): Response<CategoriesResponse>

    @GET("api/collections/film_comments/records")
    suspend fun getComments(
        @Query("filter") filter: String? = null,
        @Query("sort") sort: String? = "-created",
        @Query("page") page: Int = 1,
        @Query("perPage") perPage: Int = 30
    ): Response<CommentsResponse>

    @POST("api/collections/film_comments/records")
    suspend fun postComment(
        @Body comment: CommentRequest
    ): Response<Comment>
}

data class CommentRequest(
    val text: String,
    val film: String,
    val device: String? = null,
    val is_approved: Boolean = false
)
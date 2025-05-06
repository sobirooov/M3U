package com.m3u.feature.films

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.m3u.feature.films.pocketbase.FilmsApi
import com.m3u.feature.films.pocketbase.models.Category
import com.m3u.feature.films.pocketbase.models.Film
import com.m3u.feature.films.pocketbase.models.Comment
import com.m3u.feature.films.pocketbase.CommentRequest
import com.m3u.feature.films.pocketbase.models.CategoriesResponse
import com.m3u.feature.films.pocketbase.models.CommentsResponse
import com.m3u.feature.films.pocketbase.models.FilmsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val details: String? = null) : UiState<Nothing>()
}

data class FilmDetails(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val trailerUrl: String,
    val filmUrl: String
)

class FilmsViewModel : ViewModel() {
    private val _featuredFilmsState = MutableStateFlow<UiState<List<Film>>>(UiState.Loading)
    val featuredFilmsState: StateFlow<UiState<List<Film>>> = _featuredFilmsState

    private val _categoriesState = MutableStateFlow<UiState<List<Category>>>(UiState.Loading)
    val categoriesState: StateFlow<UiState<List<Category>>> = _categoriesState

    private val _filmsByCategoryState = MutableStateFlow<UiState<List<Film>>>(UiState.Loading)
    val filmsByCategoryState: StateFlow<UiState<List<Film>>> = _filmsByCategoryState

    private val _filmDetailsState = MutableStateFlow<UiState<FilmDetails>>(UiState.Loading)
    val filmDetailsState: StateFlow<UiState<FilmDetails>> = _filmDetailsState

    private val _commentsState = MutableStateFlow<UiState<List<Comment>>>(UiState.Loading)
    val commentsState: StateFlow<UiState<List<Comment>>> = _commentsState

    private val _messageState = MutableStateFlow<String?>(null)
    val messageState: StateFlow<String?> = _messageState

    private var lastFetchedCategoryId: String? = null
    private var lastFetchedFilmId: String? = null

    init {
        fetchFeaturedFilms()
        fetchCategories()
    }

    fun fetchFeaturedFilms() {
        if (_featuredFilmsState.value is UiState.Success) return
        _featuredFilmsState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = FilmsApi.service.getFilms(filter = "featured=true")
                handleFilmResponse(response, _featuredFilmsState, "Сара фильмлар мавжуд эмас.")
            } catch (e: Exception) {
                _featuredFilmsState.value = UiState.Error("Хато: ${e.message}", e.localizedMessage)
            }
        }
    }

    fun fetchCategories() {
        if (_categoriesState.value is UiState.Success) return
        _categoriesState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = FilmsApi.service.getCategories()
                handleCategoryResponse(response, _categoriesState, "Категориялар топилмади.")
            } catch (e: Exception) {
                _categoriesState.value = UiState.Error("Хато: ${e.message}", e.localizedMessage)
            }
        }
    }

    fun fetchFilmsByCategory(categoryId: String) {
        if (lastFetchedCategoryId == categoryId && _filmsByCategoryState.value is UiState.Success) return
        lastFetchedCategoryId = categoryId
        _filmsByCategoryState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = FilmsApi.service.getFilms(filter = "categories~'$categoryId'")
                handleFilmResponse(response, _filmsByCategoryState, "Ушбу категорияда фильмлар топилмади.")
            } catch (e: Exception) {
                _filmsByCategoryState.value = UiState.Error("Хато: ${e.message}", e.localizedMessage)
            }
        }
    }

    fun fetchFilmDetails(filmId: String) {
        if (lastFetchedFilmId == filmId && _filmDetailsState.value is UiState.Success) return
        lastFetchedFilmId = filmId
        _filmDetailsState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = FilmsApi.service.getFilms(filter = "id='$filmId'")
                if (response.isSuccessful) {
                    val film = response.body()?.items?.firstOrNull()
                    if (film != null) {
                        _filmDetailsState.value = UiState.Success(
                            FilmDetails(
                                id = film.id,
                                title = film.title,
                                description = film.description,
                                imageUrl = film.imageUrl,
                                trailerUrl = film.trailerUrl,
                                filmUrl = film.filmUrl
                            )
                        )
                        fetchComments(filmId)
                    } else {
                        _filmDetailsState.value = UiState.Error("Фильм топилмади.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _filmDetailsState.value = UiState.Error(
                        "Фильм маълумотларини олишда хatolik.",
                        "Хато ${response.code()}: ${errorBody ?: "Номаълум хато"}"
                    )
                }
            } catch (e: Exception) {
                _filmDetailsState.value = UiState.Error("Хато: ${e.message}", e.localizedMessage)
            }
        }
    }

    fun fetchComments(filmId: String) {
        _commentsState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = FilmsApi.service.getComments(filter = "film='$filmId' && is_approved=true")
                handleCommentResponse(response, _commentsState, "Тасдиқланган фикрлар мавжуд эмас.")
            } catch (e: Exception) {
                _commentsState.value = UiState.Error("Хато: ${e.message}", e.localizedMessage)
            }
        }
    }

    fun postComment(filmId: String, text: String) {
        viewModelScope.launch {
            try {
                val response = FilmsApi.service.postComment(
                    CommentRequest(text = text, film = filmId, is_approved = false)
                )
                if (response.isSuccessful) {
                    _messageState.value = "Сизнинг фикрингиз жўнатилди ва администратор тасдиғини кутмоқда."
                    fetchComments(filmId)
                } else {
                    val errorBody = response.errorBody()?.string()
                    _commentsState.value = UiState.Error(
                        "Фикрни жўнатишда хatolик.",
                        "Хато ${response.code()}: ${errorBody ?: "Номаълум хато"}"
                    )
                }
            } catch (e: Exception) {
                _commentsState.value = UiState.Error("Хато: ${e.message}", e.localizedMessage)
            }
        }
    }

    fun clearMessage() {
        _messageState.value = null
    }

    private fun handleFilmResponse(
        response: Response<FilmsResponse>,
        state: MutableStateFlow<UiState<List<Film>>>,
        emptyMessage: String
    ) {
        if (response.isSuccessful) {
            val films = response.body()?.items ?: emptyList()
            state.value = if (films.isEmpty()) UiState.Error(emptyMessage) else UiState.Success(films)
        } else {
            val errorBody = response.errorBody()?.string()
            state.value = UiState.Error(
                "Фильмларни олишда хatolик.",
                "Хато ${response.code()}: ${errorBody ?: "Номаълум хато"}"
            )
        }
    }

    private fun handleCategoryResponse(
        response: Response<CategoriesResponse>,
        state: MutableStateFlow<UiState<List<Category>>>,
        emptyMessage: String
    ) {
        if (response.isSuccessful) {
            val categories = response.body()?.items ?: emptyList()
            state.value = if (categories.isEmpty()) UiState.Error(emptyMessage) else UiState.Success(categories)
        } else {
            val errorBody = response.errorBody()?.string()
            state.value = UiState.Error(
                "Категорияларни олишда хatolик.",
                "Хато ${response.code()}: ${errorBody ?: "Номаълум хато"}"
            )
        }
    }

    private fun handleCommentResponse(
        response: Response<CommentsResponse>,
        state: MutableStateFlow<UiState<List<Comment>>>,
        emptyMessage: String
    ) {
        if (response.isSuccessful) {
            val comments = response.body()?.items ?: emptyList()
            state.value = UiState.Success(comments) // Show empty list as success
        } else {
            val errorBody = response.errorBody()?.string()
            state.value = UiState.Error(
                "Фикрларни олишда хatolик.",
                "Хато ${response.code()}: ${errorBody ?: "Номаълум хато"}"
            )
        }
    }
}
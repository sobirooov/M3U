package com.m3u.feature.films

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.m3u.feature.films.pocketbase.FilmsApi
import com.m3u.feature.films.pocketbase.models.Category
import com.m3u.feature.films.pocketbase.models.Film
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val details: String? = null) : UiState<Nothing>()
}

class FilmsViewModel : ViewModel() {
    private val _featuredFilmsState = MutableStateFlow<UiState<List<Film>>>(UiState.Loading)
    val featuredFilmsState: StateFlow<UiState<List<Film>>> = _featuredFilmsState

    private val _categoriesState = MutableStateFlow<UiState<List<Category>>>(UiState.Loading)
    val categoriesState: StateFlow<UiState<List<Category>>> = _categoriesState

    init {
        fetchFeaturedFilms()
        fetchCategories()
    }

    fun fetchFeaturedFilms() {
        _featuredFilmsState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = FilmsApi.service.getFilms(filter = "featured=true")
                if (response.isSuccessful) {
                    val films = response.body()?.items ?: emptyList()
                    if (films.isEmpty()) {
                        _featuredFilmsState.value = UiState.Error("No featured films available.")
                    } else {
                        _featuredFilmsState.value = UiState.Success(films)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "Error ${response.code()}: ${errorBody ?: "Unknown error"}"
                    _featuredFilmsState.value = UiState.Error("Failed to fetch featured films.", errorMessage)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _featuredFilmsState.value = UiState.Error("Error: ${e.message}", e.localizedMessage)
            }
        }
    }

    fun fetchCategories() {
        _categoriesState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = FilmsApi.service.getCategories()
                if (response.isSuccessful) {
                    val categories = response.body()?.items ?: emptyList()
                    if (categories.isEmpty()) {
                        _categoriesState.value = UiState.Error("No categories found.")
                    } else {
                        _categoriesState.value = UiState.Success(categories)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "Error ${response.code()}: ${errorBody ?: "Unknown error"}"
                    _categoriesState.value = UiState.Error("Failed to fetch categories.", errorMessage)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _categoriesState.value = UiState.Error("Error: ${e.message}", e.localizedMessage)
            }
        }
    }



    private val _filmsByCategoryState = MutableStateFlow<UiState<List<Film>>>(UiState.Loading)
    val filmsByCategoryState: StateFlow<UiState<List<Film>>> = _filmsByCategoryState

    fun fetchFilmsByCategory(categoryId: String) {
        _filmsByCategoryState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = FilmsApi.service.getFilms(filter = "categories~'$categoryId'")
                if (response.isSuccessful) {
                    val films = response.body()?.items ?: emptyList()
                    if (films.isEmpty()) {
                        _filmsByCategoryState.value = UiState.Error("No films found in this category.")
                    } else {
                        _filmsByCategoryState.value = UiState.Success(films)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "Error ${response.code()}: ${errorBody ?: "Unknown error"}"
                    _filmsByCategoryState.value = UiState.Error("Failed to fetch films.", errorMessage)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _filmsByCategoryState.value = UiState.Error("Error: ${e.message}", e.localizedMessage)
            }
        }
    }


}

package com.m3u.feature.films

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.m3u.feature.films.pocketbase.models.Film

@Composable
fun FilmsListRoute(
    categoryId: String,
    modifier: Modifier = Modifier,
    viewModel: FilmsViewModel = hiltViewModel()
) {
    viewModel.fetchFilmsByCategory(categoryId)
    val filmsState = viewModel.filmsByCategoryState.collectAsState()

    FilmsListScreen(
        filmsState = filmsState.value,
        onFilmClick = { /* Handle film click */ },
        modifier = modifier
    )
}

@Composable
fun FilmsListScreen(
    filmsState: UiState<List<Film>>,
    onFilmClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (filmsState) {
        is UiState.Loading -> CenteredLoader()
        is UiState.Error -> CenteredError(filmsState.message, filmsState.details)
        is UiState.Success -> {
            val films = filmsState.data
            LazyVerticalGrid(
                columns = GridCells.Adaptive(160.dp),
                contentPadding = PaddingValues(16.dp),
                modifier = modifier.fillMaxSize()
            ) {
                items(films) { film ->
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { onFilmClick(film.id) }
                    ) {
                        Column {
                            Image(
                                painter = rememberImagePainter(film.imageUrl),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = film.title,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

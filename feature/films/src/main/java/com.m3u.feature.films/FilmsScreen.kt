package com.m3u.feature.films

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.m3u.feature.films.pocketbase.models.Category
import com.m3u.feature.films.pocketbase.models.Film

@Composable
fun FilmsRoute(
    contentPadding: PaddingValues,
    navigateToFilmList: (String) -> Unit,
    navigateToFilmDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FilmsViewModel = hiltViewModel()
) {
    val featuredFilmsState = viewModel.featuredFilmsState.collectAsState()
    val categoriesState = viewModel.categoriesState.collectAsState()

    FilmsScreen(
        featuredFilmsState = featuredFilmsState.value,
        categoriesState = categoriesState.value,
        onCategoryClick = navigateToFilmList,
        onFilmClick = navigateToFilmDetails,
        contentPadding = contentPadding,
        modifier = modifier
    )
}

@Composable
fun FilmsScreen(
    featuredFilmsState: UiState<List<Film>>,
    categoriesState: UiState<List<Category>>,
    onCategoryClick: (String) -> Unit,
    onFilmClick: (String) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = contentPadding,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            Text(
                text = "Сара фильмлар",
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 24.sp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            )
        }
        item {
            when (featuredFilmsState) {
                is UiState.Loading -> CenteredLoader()
                is UiState.Error -> CenteredError(featuredFilmsState.message, featuredFilmsState.details)
                is UiState.Success -> {
                    val featuredFilms = featuredFilmsState.data
                    if (featuredFilms.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            items(featuredFilms) { film ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .padding(end = 16.dp)
                                        .width(160.dp)
                                        .clickable { onFilmClick(film.id) }
                                ) {
                                    Column {
                                        Image(
                                            painter = rememberAsyncImagePainter(film.imageUrl),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .height(220.dp)
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp)),
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
                    } else {
                        CenteredError(message = "Сара фильмлар мавжуд эмас.")
                    }
                }
            }
        }

        item {
            Text(
                text = "Категориялар",
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 24.sp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            )
        }
        when (categoriesState) {
            is UiState.Loading -> item { CenteredLoader() }
            is UiState.Error -> item { CenteredError(categoriesState.message, categoriesState.details) }
            is UiState.Success -> {
                items(categoriesState.data) { category ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable { onCategoryClick(category.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(category.imageUrl),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = category.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = category.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CenteredLoader() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun CenteredError(message: String, details: String? = null) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            details?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Тафсилотлар: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}
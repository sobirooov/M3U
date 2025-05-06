package com.m3u.feature.films

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.m3u.feature.films.pocketbase.models.Comment
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun FilmDetailsRoute(
    filmId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FilmsViewModel = hiltViewModel()
) {
    viewModel.fetchFilmDetails(filmId)
    val filmState = viewModel.filmDetailsState.collectAsState()
    val commentsState = viewModel.commentsState.collectAsState()
    val messageState = viewModel.messageState.collectAsState()

    FilmDetailsScreen(
        filmState = filmState.value,
        commentsState = commentsState.value,
        message = messageState.value,
        onPostComment = { text -> viewModel.postComment(filmId, text) },
        onClearMessage = { viewModel.clearMessage() },
        onBack = onBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilmDetailsScreen(
    filmState: UiState<FilmDetails>,
    commentsState: UiState<List<Comment>>,
    message: String?,
    onPostComment: (String) -> Unit,
    onClearMessage: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showMessage by remember { mutableStateOf(false) }

    LaunchedEffect(message) {
        if (message != null) {
            showMessage = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* No title */ },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Орқага")
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = SnackbarHostState()) { data ->
                Snackbar(
                    action = {
                        TextButton(onClick = {
                            showMessage = false
                            onClearMessage()
                        }) {
                            Text("Яхши")
                        }
                    }
                ) {
                    Text(message ?: "")
                }
            }
        }
    ) { paddingValues ->
        when (filmState) {
            is UiState.Loading -> CenteredLoader()
            is UiState.Error -> CenteredError(filmState.message, filmState.details)
            is UiState.Success -> {
                val film = filmState.data
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        // YouTube Player
                        YouTubePlayer(
                            youtubeUrl = film.trailerUrl,
                            lifecycle = lifecycleOwner.lifecycle
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = film.title,
                            style = MaterialTheme.typography.headlineMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = film.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(film.filmUrl))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Фильмни Қидириш")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Фикрлар",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    when (commentsState) {
                        is UiState.Loading -> item { CenteredLoader() }
                        is UiState.Error -> item { CenteredError(commentsState.message, commentsState.details) }
                        is UiState.Success -> {
                            items(commentsState.data) { comment ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = comment.text,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "Жойлаштирилган: ${comment.created}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                            item {
                                var commentText by remember { mutableStateOf("") }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = commentText,
                                        onValueChange = { commentText = it },
                                        label = { Text("Фикр қўшиш") },
                                        modifier = Modifier.weight(1f),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            if (commentText.isNotBlank()) {
                                                onPostComment(commentText)
                                                commentText = ""
                                            }
                                        }
                                    ) {
                                        Text("Жўнатиш")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showMessage && message != null) {
            LaunchedEffect(Unit) {
                // This will trigger the snackbar via the host
            }
        }
    }
}

@Composable
fun YouTubePlayer(
    youtubeUrl: String,
    lifecycle: Lifecycle
) {
    val videoId = extractYouTubeVideoId(youtubeUrl) ?: return

    AndroidView(
        factory = { context ->
            YouTubePlayerView(context).apply {
                lifecycle.addObserver(this)
                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.cueVideo(videoId, 0f)
                    }
                })
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f),
        update = { view ->
            lifecycle.addObserver(view)
        }
    )
}

fun extractYouTubeVideoId(url: String): String? {
    val regex = "(?<=watch\\?v=|/videos/|embed/|youtu.be/|/v/|/e/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#&?\\n]*"
    val pattern = java.util.regex.Pattern.compile(regex)
    val matcher = pattern.matcher(url)
    return if (matcher.find()) matcher.group() else null
}
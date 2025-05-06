package com.m3u.feature.films.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.m3u.feature.films.FilmDetailsRoute
import com.m3u.feature.films.FilmsRoute
import com.m3u.feature.films.FilmsListRoute

object FilmsNavigation {
    const val ROOT_ROUTE = "films_root"
    const val FILMS_ROUTE = "films"
    const val FILMS_LIST_ROUTE = "films/list/{categoryId}"
    const val FILM_DETAILS_ROUTE = "films/details/{filmId}"

    fun filmsListRoute(categoryId: String): String = "films/list/$categoryId"
    fun filmDetailsRoute(filmId: String): String = "films/details/$filmId"
}

fun NavGraphBuilder.filmsGraph(
    navController: NavController,
    contentPadding: PaddingValues
) {
    navigation(
        startDestination = FilmsNavigation.FILMS_ROUTE,
        route = FilmsNavigation.ROOT_ROUTE
    ) {
        composable(FilmsNavigation.FILMS_ROUTE) {
            FilmsRoute(
                contentPadding = contentPadding,
                navigateToFilmList = { categoryId ->
                    navController.navigate(FilmsNavigation.filmsListRoute(categoryId))
                },
                navigateToFilmDetails = { filmId ->
                    navController.navigate(FilmsNavigation.filmDetailsRoute(filmId))
                }
            )
        }
        composable(FilmsNavigation.FILMS_LIST_ROUTE) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            FilmsListRoute(
                categoryId = categoryId,
                navigateToFilmDetails = { filmId ->
                    navController.navigate(FilmsNavigation.filmDetailsRoute(filmId))
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(FilmsNavigation.FILM_DETAILS_ROUTE) { backStackEntry ->
            val filmId = backStackEntry.arguments?.getString("filmId") ?: ""
            FilmDetailsRoute(
                filmId = filmId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
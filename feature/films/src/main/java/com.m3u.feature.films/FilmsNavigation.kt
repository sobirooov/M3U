package com.m3u.feature.films.navigation

object FilmsNavigation {
    const val FilmsRoute = "films"
    const val FilmsListRoute = "films/list/{categoryId}"

    // Helper function to create the route for a specific category
    fun filmsListRoute(categoryId: String): String = "films/list/$categoryId"
}

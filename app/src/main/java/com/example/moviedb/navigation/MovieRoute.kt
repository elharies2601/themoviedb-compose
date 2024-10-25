package com.example.moviedb.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class MovieRoute(val route: String) {
    data object Home : MovieRoute("home")
    data object MovieDetail : MovieRoute("movie_detail/{movie_id}") {
        fun createRoute(movieId: Long) = "movie_detail/$movieId"
    }
    data object Review : MovieRoute("review/{movie_id}") {
        fun createRoute(movieId: Long) = "review/$movieId"
    }
}
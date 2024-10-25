package com.example.moviedb.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moviedb.ui.detail.DetailMovieScreen
import com.example.moviedb.ui.home.HomeScreen

@Composable
fun MovieNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = MovieRoute.Home.route) {
        composable(MovieRoute.Home.route) {
            HomeScreen(navHostController = navController)
        }
        composable(MovieRoute.MovieDetail.route) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movie_id")?.toLong() ?: 0L
            DetailMovieScreen(navHostController = navController, movieId = movieId)
        }
        composable(MovieRoute.Review.route) {}
    }
}
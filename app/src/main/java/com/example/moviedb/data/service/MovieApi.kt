package com.example.moviedb.data.service

import com.example.moviedb.data.model.detail.DetailMovieRes
import com.example.moviedb.data.model.genre.GenreResponse
import com.example.moviedb.data.model.movie.DiscoverRes
import com.example.moviedb.data.model.review.ReviewsRes
import com.example.moviedb.data.model.video.VideosRes
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {
    @GET("genre/movie/list")
    suspend fun getGenres(): GenreResponse

    @GET("discover/movie")
    suspend fun getMoviesByGenre(@Query("with_genres") genreId: String): DiscoverRes

    @GET("discover/movie")
    suspend fun getMoviesByGenre(@Query("with_genres") genreId: String, @Query("page") page: Int): DiscoverRes

    @GET("movie/{movie_id}")
    suspend fun getMovieById(@Path("movie_id") movieId: Long): DetailMovieRes

    @GET("movie/{movie_id}/videos")
    suspend fun getVideosByMovieId(@Path("movie_id") movieId: Long): VideosRes

    @GET("movie/{movie_id}/reviews")
    suspend fun getReviews(@Path("movie_id") movieId: Long, @Query("page") page: Int): ReviewsRes
}
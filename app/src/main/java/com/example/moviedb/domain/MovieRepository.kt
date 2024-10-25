package com.example.moviedb.domain

import com.example.moviedb.data.model.NetworkResult
import com.example.moviedb.data.model.detail.DetailMovieRes
import com.example.moviedb.data.model.genre.GenreResponse
import com.example.moviedb.data.model.movie.DiscoverRes
import com.example.moviedb.data.model.review.ReviewsRes
import com.example.moviedb.data.model.video.VideosRes

interface MovieRepository {
    suspend fun getGenres(): NetworkResult<GenreResponse>
    suspend fun getMoviesByGenre(ids: String): NetworkResult<DiscoverRes>
    suspend fun getMoviesByGenre(ids: String, page: Int): DiscoverRes
    suspend fun getDetailMovie(movieId: Long): NetworkResult<DetailMovieRes>
    suspend fun getVideosByMovieId(movieId: Long): NetworkResult<VideosRes>
    suspend fun getReviews(movieId: Long, page: Int): ReviewsRes
}
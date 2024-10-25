package com.example.moviedb.data.repository

import com.example.moviedb.data.model.NetworkResult
import com.example.moviedb.data.model.detail.DetailMovieRes
import com.example.moviedb.data.model.genre.GenreResponse
import com.example.moviedb.data.model.movie.DiscoverRes
import com.example.moviedb.data.model.review.ReviewsRes
import com.example.moviedb.data.model.video.VideosRes
import com.example.moviedb.data.service.MovieApi
import com.example.moviedb.domain.MovieRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(private val api: MovieApi): MovieRepository {

    override suspend fun getGenres(): NetworkResult<GenreResponse> {
        return try {
            NetworkResult.Success(api.getGenres())
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "")
        }
    }

    override suspend fun getMoviesByGenre(ids: String): NetworkResult<DiscoverRes> {
        return try {
            val result = api.getMoviesByGenre(ids)
            NetworkResult.Success(result)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "")
        }
    }

    override suspend fun getMoviesByGenre(ids: String, page: Int): DiscoverRes {
        return api.getMoviesByGenre(ids, page)
    }

    override suspend fun getDetailMovie(movieId: Long): NetworkResult<DetailMovieRes> {
        return try {
            val result = api.getMovieById(movieId)
            NetworkResult.Success(result)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "")
        }
    }

    override suspend fun getVideosByMovieId(movieId: Long): NetworkResult<VideosRes> {
        return try {
            val result = api.getVideosByMovieId(movieId)
            NetworkResult.Success(result)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "")
        }
    }

    override suspend fun getReviews(movieId: Long, page: Int): ReviewsRes {
        return api.getReviews(movieId, page)
    }
}
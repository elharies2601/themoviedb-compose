package com.example.moviedb.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.moviedb.data.model.review.Review
import com.example.moviedb.domain.MovieRepository
import javax.inject.Inject

class ReviewPagingSource @Inject constructor(private val repository: MovieRepository, private val movieId: Long): PagingSource<Int, Review>() {
    override fun getRefreshKey(state: PagingState<Int, Review>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Review> {
        return try {
            val page = params.key ?: 1
            val response = repository.getReviews(movieId, page)

            LoadResult.Page(
                data = response.results,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.results.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
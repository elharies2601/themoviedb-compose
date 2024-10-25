package com.example.moviedb.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.moviedb.data.model.NetworkResult
import com.example.moviedb.data.model.UiState
import com.example.moviedb.data.model.detail.DetailMovieRes
import com.example.moviedb.data.model.review.Review
import com.example.moviedb.data.model.video.VideosRes
import com.example.moviedb.data.paging.ReviewPagingSource
import com.example.moviedb.domain.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailMovieViewModel @Inject constructor(private val repository: MovieRepository) :
    ViewModel() {
    private val _detail: MutableStateFlow<UiState<DetailMovieRes>> =
        MutableStateFlow(UiState.Idle)
    val detail: StateFlow<UiState<DetailMovieRes>>
        get() = _detail.asStateFlow()

    private val _videoThumb: MutableStateFlow<UiState<VideosRes>> =
        MutableStateFlow(UiState.Idle)
    val videoThumb: StateFlow<UiState<VideosRes>>
        get() = _videoThumb.asStateFlow()

    private val _reviews: MutableStateFlow<PagingData<Review>> =
        MutableStateFlow(PagingData.empty())
    val reviews: StateFlow<PagingData<Review>>
        get() = _reviews.asStateFlow()

    fun fetchReviewsMovie(movieId: Long) {
        viewModelScope.launch {
            Pager(config = PagingConfig(pageSize = 5), pagingSourceFactory = {
                ReviewPagingSource(repository, movieId)
            }).flow.cachedIn(viewModelScope).collectLatest {
                _reviews.value = it
            }
        }
    }

    fun loadDetail(movieId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _detail.value = UiState.Loading
            when (val res = repository.getDetailMovie(movieId)) {
                is NetworkResult.Success -> {
                    _detail.value = UiState.Success(res.data)
                }
                is NetworkResult.Error -> {
                    _detail.value = UiState.Failed(res.message)
                }
                else -> {}
            }
        }
    }

    fun loadThumbnail(movieId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _videoThumb.value = UiState.Loading
            when (val res = repository.getVideosByMovieId(movieId)) {
                is NetworkResult.Success -> {
                    _videoThumb.value = UiState.Success(res.data)
                }
                is NetworkResult.Error -> {
                    _videoThumb.value = UiState.Failed(res.message)
                }
                else -> {}
            }
        }
    }
}
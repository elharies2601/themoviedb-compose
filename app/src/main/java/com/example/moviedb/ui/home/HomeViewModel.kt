package com.example.moviedb.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.moviedb.data.model.NetworkResult
import com.example.moviedb.data.model.UiState
import com.example.moviedb.data.model.genre.GenreResponse
import com.example.moviedb.data.model.movie.MovieRes
import com.example.moviedb.data.paging.MoviePagingSource
import com.example.moviedb.domain.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: MovieRepository) : ViewModel() {
    private val _genres: MutableStateFlow<UiState<MutableList<GenreResponse.Genre>>> =
        MutableStateFlow(UiState.Idle)
    val genres: StateFlow<UiState<MutableList<GenreResponse.Genre>>>
        get() = _genres.asStateFlow()

    private val _selectedGenres = MutableStateFlow<Set<GenreResponse.Genre>>(emptySet())
    val selectedGenres: StateFlow<Set<GenreResponse.Genre>> = _selectedGenres.asStateFlow()

    private val _moviesPaging: MutableStateFlow<PagingData<MovieRes>> =
        MutableStateFlow(PagingData.empty())
    val moviesPaging: StateFlow<PagingData<MovieRes>>
        get() = _moviesPaging.asStateFlow()

    init {
        loadGenres()
    }

    fun fetchMoviesByGenre(genreId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Pager(config = PagingConfig(pageSize = 10), pagingSourceFactory = {
                MoviePagingSource(repository = repository, genreId = genreId)
            }).flow.cachedIn(viewModelScope).collectLatest {
                _moviesPaging.value = it
            }
        }
    }

    private fun loadGenres() {
        viewModelScope.launch(Dispatchers.IO) {
            _genres.value = UiState.Loading
            when (val res = repository.getGenres()) {
                is NetworkResult.Success -> {
                    _genres.value = UiState.Success(res.data.genres)
                }

                is NetworkResult.Error -> {
                    _genres.value = UiState.Failed(res.message)
                }

                else -> {}
            }
        }
    }

    fun toggleGenre(genre: GenreResponse.Genre) {
        _selectedGenres.update { currentSelected ->
            if (currentSelected.contains(genre)) {
                currentSelected - genre
            } else {
                currentSelected + genre
            }
        }
    }
}
package com.example.moviedb.data.model

interface UiState<out T> {
    data object Loading: UiState<Nothing>
    data object Idle: UiState<Nothing>
    data class Success<out T>(val result: T): UiState<T>
    data class Failed(val message: String): UiState<Nothing>
}
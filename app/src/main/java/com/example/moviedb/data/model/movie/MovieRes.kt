package com.example.moviedb.data.model.movie

import com.google.gson.annotations.SerializedName

data class MovieRes(
    @SerializedName("id")
    val id: Long = 0L,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("poster_path")
    val posterPath: String = "",
    @SerializedName("backdrop_path")
    val backdropPath: String = "",
)

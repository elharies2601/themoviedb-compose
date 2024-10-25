package com.example.moviedb.data.model.detail

import com.google.gson.annotations.SerializedName

data class DetailMovieRes(
    @SerializedName("id")
    val id: Long = 0L,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("overview")
    val overview: String = "",
    @SerializedName("vote_average")
    val voteAverage: Float = 0F,
    @SerializedName("release_date")
    val releaseDate: String = "",
    @SerializedName("backdrop_path")
    val backdropPath: String = "",
)

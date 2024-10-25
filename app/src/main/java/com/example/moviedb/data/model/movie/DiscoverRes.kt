package com.example.moviedb.data.model.movie

import com.google.gson.annotations.SerializedName

data class DiscoverRes(
    @SerializedName("page")
    val page: Int = 1,
    @SerializedName("results")
    val results: MutableList<MovieRes> = mutableListOf()
)

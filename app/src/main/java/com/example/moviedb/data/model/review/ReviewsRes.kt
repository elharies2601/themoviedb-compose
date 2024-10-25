package com.example.moviedb.data.model.review

import com.google.gson.annotations.SerializedName

data class ReviewsRes(
    @SerializedName("page")
    val page: Int = 0,
    @SerializedName("results")
    val results: MutableList<Review> = mutableListOf()
)

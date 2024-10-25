package com.example.moviedb.data.model.review

import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("author")
    val author: String = "",
    @SerializedName("content")
    val content: String = ""
)

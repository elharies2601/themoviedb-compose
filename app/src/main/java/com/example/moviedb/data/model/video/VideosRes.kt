package com.example.moviedb.data.model.video

import com.google.gson.annotations.SerializedName

data class VideosRes(
    @SerializedName("results")
    val results: MutableList<Video> = mutableListOf(),
    @SerializedName("id")
    val id: Long = 0L,
) {
    data class Video(
        @SerializedName("key")
        val key: String = ""
    )
}

package com.example.moviedb.data.model.genre

import com.google.gson.annotations.SerializedName

data class GenreResponse(
    @SerializedName("genres")
    val genres: MutableList<Genre> = mutableListOf()
) {
    data class Genre(
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("name")
        val name: String = ""
    )
}

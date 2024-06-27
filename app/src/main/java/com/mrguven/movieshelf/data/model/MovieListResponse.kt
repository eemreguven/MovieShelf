package com.mrguven.movieshelf.data.model

import com.google.gson.annotations.SerializedName
import com.mrguven.movieshelf.data.model.Movie

data class MovieListResponse(
    @SerializedName("page") val page: Int?,
    @SerializedName("results") val results: List<Movie>?,
    @SerializedName("total_pages") val totalPages: Int?,
    @SerializedName("total_results") val totalResults: Int?,
)
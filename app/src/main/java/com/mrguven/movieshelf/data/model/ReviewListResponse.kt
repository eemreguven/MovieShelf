package com.mrguven.movieshelf.data.model

import com.google.gson.annotations.SerializedName
import com.mrguven.movieshelf.data.model.Review

data class ReviewListResponse (
    @SerializedName("page") val page: Int?,
    @SerializedName("results") val results: List<Review>?,
    @SerializedName("total_pages") val totalPages: Int?,
    @SerializedName("total_results") val totalResults: Int?,
)
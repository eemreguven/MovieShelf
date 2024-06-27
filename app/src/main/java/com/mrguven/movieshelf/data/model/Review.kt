package com.mrguven.movieshelf.data.model

import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("author") val author: String?,
    @SerializedName("content") val content: String?,
)

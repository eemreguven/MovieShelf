package com.mrguven.movieshelf.data.model

import com.google.gson.annotations.SerializedName
import com.mrguven.movieshelf.data.model.Video

data class VideoListResponse(
    @SerializedName("results") val results: List<Video>?,
)

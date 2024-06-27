package com.mrguven.movieshelf.data.model

import com.google.gson.annotations.SerializedName
import com.mrguven.movieshelf.data.model.Genre

data class MovieDetails(
    @SerializedName("id") val id: Int?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("genres") val genres: List<Genre>?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("popularity") val popularity: Double?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("runtime") val runtime: Int?,
    @SerializedName("tagline") val tagline: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    var isFavorite: Boolean = false,
    var isInWatchList: Boolean = false
)

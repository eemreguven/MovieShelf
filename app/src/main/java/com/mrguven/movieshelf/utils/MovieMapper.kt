package com.mrguven.movieshelf.utils

import com.mrguven.movieshelf.data.local.MovieEntity
import com.mrguven.movieshelf.data.model.Movie
import com.mrguven.movieshelf.data.model.MovieDetails

object MovieMapper {
    fun toMovieEntity(movie: Movie): MovieEntity {
        return MovieEntity(
            id = movie.id ?: 0,
            posterPath = movie.posterPath ?: "",
            title = movie.title ?: "",
            popularity = movie.popularity ?: 0.0,
            releaseDate = movie.releaseDate ?: "",
            voteAverage = movie.voteAverage ?: 0.0,
            isFavorite = movie.isFavorite,
            isInWatchList = movie.isInWatchList,
        )
    }

    fun toMovie(movieEntity: MovieEntity): Movie {
        return Movie(
            id = movieEntity.id,
            posterPath = movieEntity.posterPath,
            title = movieEntity.title,
            popularity = movieEntity.popularity,
            releaseDate = movieEntity.releaseDate,
            voteAverage = movieEntity.voteAverage,
            isFavorite = movieEntity.isFavorite,
            isInWatchList = movieEntity.isInWatchList
        )
    }

    fun detailsToMovieEntity(movieDetails: MovieDetails): MovieEntity {
        return MovieEntity(
            id = movieDetails.id ?: 0,
            posterPath = movieDetails.posterPath ?: "",
            title = movieDetails.title ?: "",
            popularity = movieDetails.popularity ?: 0.0,
            releaseDate = movieDetails.releaseDate ?: "",
            voteAverage = movieDetails.voteAverage ?: 0.0,
            isFavorite = movieDetails.isFavorite,
            isInWatchList = movieDetails.isInWatchList,
        )
    }

    fun detailsToMovie(movieDetails: MovieDetails): Movie {
        return Movie(
            id = movieDetails.id ?: 0,
            posterPath = movieDetails.posterPath ?: "",
            title = movieDetails.title ?: "",
            popularity = movieDetails.popularity ?: 0.0,
            releaseDate = movieDetails.releaseDate ?: "",
            voteAverage = movieDetails.voteAverage ?: 0.0,
            isFavorite = movieDetails.isFavorite,
            isInWatchList = movieDetails.isInWatchList,
        )
    }
}

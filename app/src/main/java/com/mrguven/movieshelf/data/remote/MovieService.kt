package com.mrguven.movieshelf.data.remote

import com.mrguven.movieshelf.data.model.MovieDetails
import com.mrguven.movieshelf.data.model.MovieListResponse
import com.mrguven.movieshelf.data.model.ReviewListResponse
import com.mrguven.movieshelf.data.model.VideoListResponse
import com.mrguven.movieshelf.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Header("Authorization") authorizationHeader: String = Constants.AUTHORIZATION_HEADER,
        @Header("Accept") acceptHeader: String = Constants.ACCEPT_HEADER,
        @Query("page") page: Int
    ): MovieListResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Header("Authorization") authorizationHeader: String = Constants.AUTHORIZATION_HEADER,
        @Header("Accept") acceptHeader: String = Constants.ACCEPT_HEADER,
        @Query("query") query: String,
        @Query("page") page: Int
    ): MovieListResponse

    @GET("movie/{movieId}")
    suspend fun getMovieDetails(
        @Header("Authorization") authorizationHeader: String = Constants.AUTHORIZATION_HEADER,
        @Header("Accept") acceptHeader: String = Constants.ACCEPT_HEADER,
        @Path("movieId") movieId: String,
    ): MovieDetails

    @GET("movie/{movieId}/reviews")
    suspend fun getMovieReviews(
        @Header("Authorization") authorizationHeader: String = Constants.AUTHORIZATION_HEADER,
        @Header("Accept") acceptHeader: String = Constants.ACCEPT_HEADER,
        @Path("movieId") movieId: String,
        @Query("page") page: Int,
    ): ReviewListResponse

    @GET("movie/{movieId}/videos")
    suspend fun getMovieVideos(
        @Header("Authorization") authorizationHeader: String = Constants.AUTHORIZATION_HEADER,
        @Header("Accept") acceptHeader: String = Constants.ACCEPT_HEADER,
        @Path("movieId") movieId: String,
    ): VideoListResponse
}
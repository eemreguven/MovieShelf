package com.mrguven.movieshelf.data.repository

import com.mrguven.movieshelf.data.local.MovieDao
import com.mrguven.movieshelf.data.local.MovieEntity
import com.mrguven.movieshelf.data.model.Movie
import com.mrguven.movieshelf.data.model.MovieDetails
import com.mrguven.movieshelf.data.model.Review
import com.mrguven.movieshelf.data.model.Video
import com.mrguven.movieshelf.data.remote.MovieService
import com.mrguven.movieshelf.utils.MovieMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val movieService: MovieService, private val movieDao: MovieDao
) {
    // Fetch popular movies from remote service and save to database
    suspend fun fetchPopularMovies(page: Int): List<Movie>? = try {
        val response = movieService.getPopularMovies(page = page)
        response.results?.let { saveListToDatabase(it) }
        response.results ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    // Search for movies based on query and page number
    suspend fun searchMovies(query: String, page: Int): List<Movie> = try {
        val response = movieService.searchMovies(query = query, page = page)
        response.results?.let { updateFromDatabase(it) } ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    // Fetch movie details from remote service and update local database
    suspend fun fetchMovieDetails(movieId: String): MovieDetails? = try {
        val movieDetails = movieService.getMovieDetails(movieId = movieId)
        val movieEntity = movieDao.getMovieById(movieId.toInt())
        if (movieEntity != null) {
            movieDetails.isFavorite = movieEntity.isFavorite
            movieDetails.isInWatchList = movieEntity.isInWatchList
        } else {
            movieDao.insert(MovieMapper.detailsToMovieEntity(movieDetails))
        }
        movieDetails
    } catch (e: Exception) {
        null
    }

    // Fetch movie reviews from remote service
    suspend fun fetchMovieReviews(movieId: String): List<Review>? = try {
        val response = movieService.getMovieReviews(movieId = movieId, page = 1)
        if (response.totalResults == 0) {
            emptyList()
        } else {
            response.results ?: emptyList()
        }
    } catch (e: Exception) {
        emptyList()
    }

    // Fetch movie videos from remote service
    suspend fun fetchMovieVideos(movieId: String): List<Video>? = try {
        val response = movieService.getMovieVideos(movieId = movieId)
        response.results ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    // Observe movies in range from local database
    fun observeMoviesInRangeFromDatabase(startIndex: Int, count: Int): Flow<List<Movie>?> {
        return movieDao.observePopularMoviesInRange(startIndex, count).map { list ->
            list.map { MovieMapper.toMovie(it) }
        }
    }

    // Observe favorite movies from local database
    fun observeFavoriteMoviesFromDatabase(): Flow<List<MovieEntity>?> {
        return movieDao.observeAllFavoriteMovies().map { list ->
            list.map { it }
        }
    }

    // Observe movies in watch list from local database
    fun observeInWatchListMoviesFromDatabase(): Flow<List<MovieEntity>?> {
        return movieDao.observeAllInWatchListMovies().map { list ->
            list.map { it }
        }
    }

    // Save a list of movies to the local database
    private suspend fun saveListToDatabase(newMovies: List<Movie>) {
        for (movie in newMovies) {
            val movieFromDb = movie.id?.let { movieDao.getMovieById(it) }
            if (movieFromDb == null) {
                movieDao.insert(MovieMapper.toMovieEntity(movie))
            } else {
                if (movieFromDb.popularity != movie.popularity || movieFromDb.voteAverage != movie.popularity) {
                    movieFromDb.popularity = movie.popularity ?: 0.0
                    movieFromDb.voteAverage = movie.voteAverage ?: 0.0
                    movieDao.insert(movieFromDb)
                }
            }
        }
    }

    // Update the local database with new movies and their statuses
    private suspend fun updateFromDatabase(newMovies: List<Movie>): List<Movie> {
        for (movie in newMovies) {
            val movieFromDb = movie.id?.let { movieDao.getMovieById(it) }
            movieFromDb?.let {
                movie.isFavorite = it.isFavorite
                movie.isInWatchList = it.isInWatchList
            }
        }
        return newMovies
    }

    // Update the favorite status of a movie in the local database
    suspend fun updateFavoriteStatus(movie: Movie) {
        val dbMovie = movieDao.getMovieById(movie.id!!)
        if (dbMovie != null) {
            dbMovie.isFavorite = !dbMovie.isFavorite
            movieDao.update(dbMovie)
        } else {
            movie.isFavorite = true
            movieDao.insert(MovieMapper.toMovieEntity(movie))
        }
    }

    // Update the watch list status of a movie in the local database
    suspend fun updateWatchListStatus(movie: Movie) {
        val dbMovie = movieDao.getMovieById(movie.id!!)
        if (dbMovie != null) {
            dbMovie.isInWatchList = !dbMovie.isInWatchList
            movieDao.update(dbMovie)
        } else {
            movie.isInWatchList = true
            movieDao.insert(MovieMapper.toMovieEntity(movie))
        }
    }
}

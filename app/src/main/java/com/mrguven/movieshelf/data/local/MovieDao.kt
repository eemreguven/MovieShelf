package com.mrguven.movieshelf.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movie_table ORDER BY popularity DESC LIMIT :startIndex, :count")
    fun observePopularMoviesInRange(startIndex: Int, count: Int): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movie_table WHERE isFavorite = 1")
    fun observeAllFavoriteMovies():  Flow<List<MovieEntity>>

    @Query("SELECT * FROM movie_table WHERE isInWatchList = 1")
    fun observeAllInWatchListMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movie_table WHERE id = :movieId")
    suspend fun getMovieById(movieId: Int): MovieEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: MovieEntity)

    @Delete
    suspend fun delete(movie: MovieEntity)

    @Update
    suspend fun update(movie: MovieEntity)
}

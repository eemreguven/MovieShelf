package com.mrguven.movieshelf.di

import com.mrguven.movieshelf.data.local.MovieDao
import com.mrguven.movieshelf.data.remote.MovieService
import com.mrguven.movieshelf.data.repository.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideMovieRepository(
        movieService: MovieService, movieDao: MovieDao
    ): MovieRepository {
        return MovieRepository(movieService, movieDao)
    }
}

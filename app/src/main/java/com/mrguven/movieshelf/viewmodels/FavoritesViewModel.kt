package com.mrguven.movieshelf.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrguven.movieshelf.data.local.MovieEntity
import com.mrguven.movieshelf.data.repository.MovieRepository
import com.mrguven.movieshelf.utils.MovieMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(private val movieRepository: MovieRepository) :
    ViewModel() {

    private val _displayedMoviesLiveData = MutableLiveData<List<MovieEntity>>()
    val displayedMoviesLiveData: LiveData<List<MovieEntity>> get() = _displayedMoviesLiveData
    val liveDataLoadingState = MutableLiveData(false)

    init {
        observeFavoriteMovies()
    }

    // Observe favorite movies from the database
    fun observeFavoriteMovies() {
        viewModelScope.launch {
            movieRepository.observeFavoriteMoviesFromDatabase().collect { favoriteMovies ->
                _displayedMoviesLiveData.value = favoriteMovies ?: emptyList()
            }
        }
    }

    // Handle the favorite button click event
    fun onFavoriteButtonClicked(movieEntity: MovieEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            movieRepository.updateFavoriteStatus(MovieMapper.toMovie(movieEntity))
        }
    }

    // Handle the watch list button click event
    fun onWatchListButtonClicked(movieEntity: MovieEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            movieRepository.updateWatchListStatus(MovieMapper.toMovie(movieEntity))
        }
    }
}

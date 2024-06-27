package com.mrguven.movieshelf.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrguven.movieshelf.data.model.Movie
import com.mrguven.movieshelf.data.model.MovieDetails
import com.mrguven.movieshelf.data.model.Review
import com.mrguven.movieshelf.data.model.Video
import com.mrguven.movieshelf.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(private val movieRepository: MovieRepository) :
    ViewModel() {

    private val _liveDataLoadingState = MutableLiveData(false)
    val liveDataLoadingState: LiveData<Boolean>
        get() = _liveDataLoadingState

    private val _reviews = MutableLiveData<List<Review>?>()
    val reviews: MutableLiveData<List<Review>?> = _reviews

    private val _videos = MutableLiveData<List<Video>?>()
    val videos: MutableLiveData<List<Video>?> = _videos

    private val _details = MutableLiveData<MovieDetails?>()
    val details: MutableLiveData<MovieDetails?> = _details

    // Fetch movie details from the repository and update LiveData
    fun fetchMovieDetails(movieId: String) {
        viewModelScope.launch {
            val detailsResponse = movieRepository.fetchMovieDetails(movieId)
            _details.postValue(detailsResponse)
        }
    }

    // Fetch movie reviews from the repository and update LiveData
    fun fetchMovieReviews(movieId: String) {
        viewModelScope.launch {
            val reviewsResponse = movieRepository.fetchMovieReviews(movieId)
            _reviews.postValue(reviewsResponse)
        }
    }

    // Fetch movie videos from the repository and update LiveData
    fun fetchMovieVideos(movieId: String) {
        viewModelScope.launch {
            val videosResponse = movieRepository.fetchMovieVideos(movieId)
            _videos.postValue(videosResponse)
            setLoadingState(false)
        }
    }

    // Set the loading state
    fun setLoadingState(isLoading: Boolean) {
        _liveDataLoadingState.postValue(isLoading)
    }

    // Update the favorite status of a movie
    fun onFavoriteButtonClicked(movie: Movie) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                movieRepository.updateFavoriteStatus(movie)
            }
        }
    }

    // Update the watch list status of a movie
    fun onWatchListButtonClicked(movie: Movie) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                movieRepository.updateWatchListStatus(movie)
            }
        }
    }
}

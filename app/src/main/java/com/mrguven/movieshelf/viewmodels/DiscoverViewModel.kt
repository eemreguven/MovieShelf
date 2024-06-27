package com.mrguven.movieshelf.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mrguven.movieshelf.data.model.Movie
import com.mrguven.movieshelf.data.paging.MoviesPagingSource
import com.mrguven.movieshelf.data.paging.SearchPagingSource
import com.mrguven.movieshelf.data.repository.MovieRepository
import com.mrguven.movieshelf.ui.adapters.DiscoverPagedAdapter
import com.mrguven.movieshelf.utils.LoadingStateListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(private val movieRepository: MovieRepository) :
    ViewModel(), LoadingStateListener {

    private val _liveDataViewType = MutableLiveData(DiscoverPagedAdapter.ViewType.GRID)
    val liveDataViewType: LiveData<DiscoverPagedAdapter.ViewType>
        get() = _liveDataViewType

    private val _liveDataLoadingState = MutableLiveData(false)
    val liveDataLoadingState: LiveData<Boolean>
        get() = _liveDataLoadingState

    val movieListFlow: Flow<PagingData<Movie>>
    init {
        movieListFlow = Pager(PagingConfig(pageSize = 2)) {
            MoviesPagingSource(movieRepository, this@DiscoverViewModel)
        }.flow.cachedIn(viewModelScope)
    }

    // Search movies with pagination based on query
    fun searchMoviesPaging(query: String): Flow<PagingData<Movie>> {
        return Pager(PagingConfig(pageSize = 1)) {
            SearchPagingSource(movieRepository, query = query, this@DiscoverViewModel)
        }.flow.cachedIn(viewModelScope)
    }

    // Set the loading state
    override fun setLoadingState(isLoading: Boolean) {
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

    // Toggle the view type between GRID and LIST
    fun toggleViewType() {
        _liveDataViewType.value =
            if (_liveDataViewType.value == DiscoverPagedAdapter.ViewType.GRID) {
                DiscoverPagedAdapter.ViewType.LIST
            } else {
                DiscoverPagedAdapter.ViewType.GRID
            }
    }
}

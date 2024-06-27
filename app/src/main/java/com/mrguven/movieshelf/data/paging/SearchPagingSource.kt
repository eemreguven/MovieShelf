package com.mrguven.movieshelf.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mrguven.movieshelf.data.model.Movie
import com.mrguven.movieshelf.data.repository.MovieRepository
import com.mrguven.movieshelf.utils.LoadingStateListener

class SearchPagingSource(
    private val movieRepository: MovieRepository,
    private val query: String,
    private val loadingStateListener: LoadingStateListener,
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        // Get the current page or default to the starting page index
        val page = params.key ?: STARTING_PAGE_INDEX
        return try {
            // Notify that loading has started
            loadingStateListener.setLoadingState(true)

            // Search for movies based on the query and current page
            val movies = movieRepository.searchMovies(query, page)

            // Return appropriate LoadResult based on the retrieved movies
            when {
                movies.isEmpty() -> LoadResult.Page(
                    data = emptyList(),
                    prevKey = if (page == STARTING_PAGE_INDEX) null else page.minus(1),
                    nextKey = null
                )
                else -> LoadResult.Page(
                    data = movies,
                    prevKey = if (page == STARTING_PAGE_INDEX) null else page.minus(1),
                    nextKey = page.plus(1)
                )
            }
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        } finally {
            // Indicate that loading is completed (either success or error)
            loadingStateListener.setLoadingState(false)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }
}

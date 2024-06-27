package com.mrguven.movieshelf.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mrguven.movieshelf.data.model.Movie
import com.mrguven.movieshelf.data.repository.MovieRepository
import com.mrguven.movieshelf.utils.LoadingStateListener
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException

class MoviesPagingSource(
    private val movieRepository: MovieRepository,
    private val loadingStateListener: LoadingStateListener
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        // Get the current page or default to the starting page index
        val page = params.key ?: STARTING_PAGE_INDEX
        return try {
            // Notify that loading has started
            loadingStateListener.setLoadingState(true)

            // Fetch popular movies for the current page
            movieRepository.fetchPopularMovies(page = page)

            val pageSize = PAGE_SIZE
            val startIndex = (page - 1) * pageSize

            // Retrieve movies from the database within the specified range
            val movies = movieRepository.observeMoviesInRangeFromDatabase(startIndex, pageSize).first() ?: emptyList()

            // Return appropriate LoadResult based on the retrieved movies
            when {
                movies.isEmpty() -> LoadResult.Error(NoSuchElementException("No movies found for page $page"))
                else -> LoadResult.Page(
                    data = movies,
                    prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                    nextKey = page + 1
                )
            }
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        } finally {
            // Notify that loading has finished
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
        private const val PAGE_SIZE = 20
    }
}

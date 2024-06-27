package com.mrguven.movieshelf.ui.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.mrguven.movieshelf.data.local.MovieEntity
import com.mrguven.movieshelf.databinding.FragmentWatchListBinding
import com.mrguven.movieshelf.ui.adapters.MovieListRecyclerAdapter
import com.mrguven.movieshelf.viewmodels.WatchListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WatchListFragment : Fragment() {
    private val viewModel: WatchListViewModel by viewModels()
    private lateinit var watchListRecyclerAdapter: MovieListRecyclerAdapter
    private lateinit var binding: FragmentWatchListBinding

    // Inflate the layout for this fragment and handle orientation-specific visibility
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentWatchListBinding.inflate(layoutInflater, container, false)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.toolbar.visibility = View.GONE
        } else {
            binding.toolbar.visibility = View.VISIBLE
        }
        return binding.root
    }

    // Initialize views and set up ViewModel observers when the view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        listenViewModel()
    }

    // Observe watch list movies when the fragment is started
    override fun onStart() {
        super.onStart()
        viewModel.observeWatchListMovies()
    }

    // Handle orientation-specific toolbar visibility when the fragment is resumed
    override fun onResume() {
        super.onResume()
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.toolbar.visibility = View.GONE
        } else {
            binding.toolbar.visibility = View.VISIBLE
        }
    }

    // Initialize RecyclerView and its adapter
    private fun initView() {
        watchListRecyclerAdapter = MovieListRecyclerAdapter(
            { movie -> onItemClickListener(movie) },
            viewModel::onFavoriteButtonClicked,
            viewModel::onWatchListButtonClicked
        )
        binding.watchListRecyclerView.adapter = watchListRecyclerAdapter
    }

    // Handle click events on movie items to navigate to the details screen
    private fun onItemClickListener(movie: MovieEntity) {
        val action = WatchListFragmentDirections.actionWatchListFragmentToDetailsFragment(
            movie.id.toString(), movie.title
        )
        view?.let { Navigation.findNavController(it) }?.navigate(action)
    }

    // Observe ViewModel LiveData and update the UI accordingly
    private fun listenViewModel() {
        viewModel.apply {
            displayedMoviesLiveData.observe(viewLifecycleOwner) { displayedMovies ->
                watchListRecyclerAdapter.updateList(displayedMovies)
            }
            liveDataLoadingState.observe(viewLifecycleOwner) { isLoading ->
                binding.progressBar.isVisible = isLoading
            }
        }
    }
}

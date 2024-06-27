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
import com.mrguven.movieshelf.databinding.FragmentFavoritesBinding
import com.mrguven.movieshelf.ui.adapters.MovieListRecyclerAdapter
import com.mrguven.movieshelf.viewmodels.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment() {
    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var favoritesRecyclerAdapter: MovieListRecyclerAdapter
    private lateinit var binding: FragmentFavoritesBinding

    // Inflate the layout for this fragment and handle orientation-specific visibility
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(layoutInflater, container, false)
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

    // Observe favorite movies when the fragment is started
    override fun onStart() {
        super.onStart()
        viewModel.observeFavoriteMovies()
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
        favoritesRecyclerAdapter = MovieListRecyclerAdapter(
            { movie -> onItemClickListener(movie) },
            viewModel::onFavoriteButtonClicked,
            viewModel::onWatchListButtonClicked
        )
        binding.favoritesRecyclerView.adapter = favoritesRecyclerAdapter
    }

    // Handle click events on movie items to navigate to the details screen
    private fun onItemClickListener(movie: MovieEntity) {
        val action = FavoritesFragmentDirections.actionFavoritesFragmentToDetailsFragment(
            movie.id.toString(), movie.title
        )
        view?.let { Navigation.findNavController(it) }?.navigate(action)
    }

    // Observe ViewModel LiveData and update the UI accordingly
    private fun listenViewModel() {
        viewModel.apply {
            displayedMoviesLiveData.observe(viewLifecycleOwner) { displayedMovies ->
                favoritesRecyclerAdapter.updateList(displayedMovies)
            }
            liveDataLoadingState.observe(viewLifecycleOwner) { isLoading ->
                binding.progressBar.isVisible = isLoading
            }
        }
    }
}

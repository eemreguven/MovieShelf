package com.mrguven.movieshelf.ui.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.mrguven.movieshelf.R
import com.mrguven.movieshelf.databinding.FragmentDiscoverBinding
import com.mrguven.movieshelf.ui.adapters.DiscoverPagedAdapter
import com.mrguven.movieshelf.viewmodels.DiscoverViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DiscoverFragment : Fragment() {
    private val viewModel: DiscoverViewModel by viewModels()
    private lateinit var discoverPagedAdapter: DiscoverPagedAdapter
    private lateinit var binding: FragmentDiscoverBinding

    // Inflate the layout for this fragment and handle orientation-specific visibility
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiscoverBinding.inflate(layoutInflater, container, false)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.toolbar.visibility = View.GONE
        } else {
            binding.toolbar.visibility = View.VISIBLE
        }
        return binding.root
    }

    // Setup RecyclerView, listeners, and ViewModel observers when the view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        listenViewModel()
    }

    // Refresh the adapter when the fragment is started
    override fun onStart() {
        super.onStart()
        discoverPagedAdapter.refreshAdapter()
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

    // Set up click listeners for view type button and search bar
    private fun setupListeners() {
        binding.viewTypeButton.setOnClickListener {
            viewModel.toggleViewType()
        }
        binding.movieSearchBar.setOnQueryTextListener(searchViewOnQueryTextListener)
    }

    // Initialize the RecyclerView with a paged adapter
    private fun setupRecyclerView() {
        discoverPagedAdapter = DiscoverPagedAdapter(
            viewModel.liveDataViewType,
            viewModel::onFavoriteButtonClicked,
            viewModel::onWatchListButtonClicked
        )
        binding.moviesRecyclerView.adapter = discoverPagedAdapter
    }

    // Observe ViewModel LiveData and update the UI accordingly
    private fun listenViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.apply {
                movieListFlow.collect { pagingData ->
                    discoverPagedAdapter.submitData(pagingData)
                }
            }
        }
        viewModel.apply {
            liveDataViewType.observe(viewLifecycleOwner) { viewType ->
                setLayoutManagerBasedOnViewType(viewType)
                updateViewTypeButtonIcon(viewType)
            }
            liveDataLoadingState.observe(viewLifecycleOwner) { isLoading ->
                binding.progressBar.isVisible = isLoading
            }
        }
    }

    // Set the layout manager of RecyclerView based on the current view type
    private fun setLayoutManagerBasedOnViewType(viewType: DiscoverPagedAdapter.ViewType) {
        val spanCount = if (viewType == DiscoverPagedAdapter.ViewType.GRID) {
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 4 else 2
        } else 1
        binding.moviesRecyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
    }

    // Update the icon of the view type button based on the current view type
    private fun updateViewTypeButtonIcon(viewType: DiscoverPagedAdapter.ViewType) {
        val iconResId = when (viewType) {
            DiscoverPagedAdapter.ViewType.GRID -> R.drawable.ic_grid_view
            DiscoverPagedAdapter.ViewType.LIST -> R.drawable.ic_list_view
        }
        binding.viewTypeButton.setImageResource(iconResId)
    }

    // SearchView listener for handling text changes and search submissions
    private val searchViewOnQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean = false

        override fun onQueryTextChange(newText: String): Boolean {
            val movieFlow = if (newText.isNotEmpty()) {
                viewModel.searchMoviesPaging(newText)
            } else {
                viewModel.movieListFlow
            }

            viewLifecycleOwner.lifecycleScope.launch {
                movieFlow.collectLatest { pagingData ->
                    discoverPagedAdapter.submitData(pagingData)
                }
            }
            return true
        }
    }
}

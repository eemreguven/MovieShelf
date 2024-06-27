package com.mrguven.movieshelf.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mrguven.movieshelf.R
import com.mrguven.movieshelf.data.model.Movie
import com.mrguven.movieshelf.data.model.MovieDetails
import com.mrguven.movieshelf.data.model.Review
import com.mrguven.movieshelf.data.model.Video
import com.mrguven.movieshelf.databinding.FragmentDetailsBinding
import com.mrguven.movieshelf.ui.adapters.ReviewRecyclerAdapter
import com.mrguven.movieshelf.ui.adapters.VideoRecyclerAdapter
import com.mrguven.movieshelf.utils.Constants
import com.mrguven.movieshelf.utils.FormatDate
import com.mrguven.movieshelf.utils.MovieMapper
import com.mrguven.movieshelf.viewmodels.DetailsViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private val viewModel: DetailsViewModel by viewModels()
    private lateinit var binding: FragmentDetailsBinding
    private var videoId: String = ""

    // Inflate the layout for this fragment and initialize the view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(layoutInflater, container, false)
        hideBottomNavigationView()

        val movieId = arguments?.getString(ARG_ID).toString()
        val title = arguments?.getString(ARG_TITLE).toString()

        setToolbar(title)
        fetchInitialData(movieId)
        setupViewModelObservers(movieId)

        return binding.root
    }

    // Release resources and show bottom navigation view when fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        binding.youtubePlayerView.release()
        showBottomNavigationView()
    }

    // Fetch initial data (movie details, reviews, and videos) for the specified movie ID
    private fun fetchInitialData(movieId: String) {
        viewModel.setLoadingState(true)
        viewModel.fetchMovieDetails(movieId)
        viewModel.fetchMovieReviews(movieId)
        viewModel.fetchMovieVideos(movieId)
    }

    // Set up observers for ViewModel LiveData to update the UI
    private fun setupViewModelObservers(movieId: String) {
        viewModel.apply {
            liveDataLoadingState.observe(viewLifecycleOwner) { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.progressBarLayout.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
            details.observe(viewLifecycleOwner) { details ->
                details?.let {
                    loadDetailsUI(details)
                }
                viewModel.setLoadingState(false)
            }
            reviews.observe(viewLifecycleOwner) { reviews ->
                reviews?.let {
                    loadReviews(reviews)
                }
            }
            videos.observe(viewLifecycleOwner) { videos ->
                val youtubeVideos =
                    videos?.filter { it.site == "YouTube" }?.reversed() ?: emptyList()
                val hasVideos = youtubeVideos.isNotEmpty()
                setTrailerButton(hasVideos)
                loadVideos(youtubeVideos)
            }
        }
    }

    // Load movie details into the UI
    private fun loadDetailsUI(details: MovieDetails) {
        loadPosterImage(details.posterPath ?: "")
        loadBackdropImage(details.backdropPath ?: "")
        loadInfoTexts(details)
        loadYoutubePlayerView()

        setFavoriteButton(details.isFavorite, MovieMapper.detailsToMovie(details))
        setWatchListButton(details.isInWatchList, MovieMapper.detailsToMovie(details))
    }

    // Load movie reviews into the UI
    private fun loadReviews(reviews: List<Review>) {
        binding.reviewsRecyclerView.adapter = ReviewRecyclerAdapter(reviews)
        if (reviews.isEmpty()) {
            binding.noReviewsText.visibility = View.VISIBLE
        } else {
            val reviewsTag = getString(R.string.reviews)
            val reviewsText = "$reviewsTag (${reviews.size})"
            binding.reviewsTag.text = reviewsText
            binding.noReviewsText.visibility = View.GONE
        }
    }

    // Load movie videos into the UI
    private fun loadVideos(videos: List<Video>) {
        if (videos.isNotEmpty()) {
            videoId = videos[0].key.toString()
        }
        binding.videoTitlesRecyclerView.adapter = VideoRecyclerAdapter(videos).apply {
            onItemClickListener = { playedVideoId ->
                videoId = playedVideoId
                switchYoutubePlayerView()
            }
        }
    }

    // Load poster image into the UI
    private fun loadPosterImage(posterPath: String) {
        if (posterPath.isNotBlank()) {
            val posterUrl = Constants.DETAIL_POSTER_BASE_URL + posterPath
            Glide.with(binding.poster).load(posterUrl).into(binding.poster)
        } else {
            val posterDrawableId = R.drawable.image_holder
            Glide.with(binding.poster).load(posterDrawableId).into(binding.poster)
        }
    }

    // Load backdrop image into the UI
    private fun loadBackdropImage(backdropPath: String) {
        if (backdropPath.isNotBlank()) {
            val backdropUrl = Constants.DETAIL_BACKDROP_BASE_URL + backdropPath
            Glide.with(binding.backdrop).load(backdropUrl).into(binding.backdrop)
        } else {
            val backdropDrawableId = R.drawable.image_holder
            Glide.with(binding.backdrop).load(backdropDrawableId).into(binding.backdrop)
        }
    }

    // Load various text information into the UI
    private fun loadInfoTexts(details: MovieDetails) {
        var formattedReleaseDate = details.releaseDate?.let { FormatDate.formatDate(it) }
        if (formattedReleaseDate == "No Date") formattedReleaseDate = null
        binding.releaseDate.text =
            formattedReleaseDate ?: getString(R.string.release_date_not_available)

        val formattedRuntime = details.runtime?.let { convertMinutesToHoursAndMinutes(it) }
        binding.runtime.text = formattedRuntime ?: getString(R.string.runtime_not_available)

        binding.voteAverage.text = details.voteAverage?.let { "%.1f".format(it) }
            ?: getString(R.string.vote_average_not_available)

        val genreText = if (details.genres.isNullOrEmpty()) {
            getString(R.string.genres_not_available)
        } else {
            val genreList = details.genres.mapNotNull { it.name }
            genreList.joinToString(" | ")
        }
        binding.genreList.text = genreText

        binding.tagline.text =
            if (details.tagline.isNullOrBlank()) getString(R.string.tagline_not_available)
            else details.tagline

        binding.overview.text =
            if (details.overview.isNullOrBlank()) getString(R.string.overview_not_available)
            else details.overview
    }

    // Initialize YouTube player view
    private fun loadYoutubePlayerView() {
        binding.youtubePlayerView.initialize(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.cueVideo(videoId, 0f)
            }
        })
        lifecycle.addObserver(binding.youtubePlayerView)
    }

    // Switch YouTube player view to a new video
    private fun switchYoutubePlayerView() {
        binding.youtubePlayerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                // Load the new video
                youTubePlayer.cueVideo(videoId, 0f)
            }
        })
    }

    // Set the state and click listener of the trailer button
    private fun setTrailerButton(hasVideos: Boolean) {
        val trailerButtonIcon =
            if (binding.youtubePlayerView.isVisible) R.drawable.ic_play_trailer_filled else R.drawable.ic_play_trailer
        binding.trailerButton.setImageResource(trailerButtonIcon)

        if (hasVideos) {
            binding.trailerButton.isEnabled = true
            binding.trailerButton.setOnClickListener {
                setInfoVisibility()
                setTrailerButton(true)
            }
        } else {
            binding.trailerButton.setOnClickListener {
                Toast.makeText(
                    requireContext(), getString(R.string.no_videos_available), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Toggle visibility of various UI elements
    private fun setInfoVisibility() {
        binding.poster.visibility =
            if (binding.poster.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        binding.info.visibility =
            if (binding.info.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        binding.youtubePlayerView.visibility =
            if (binding.youtubePlayerView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        binding.videoTitlesRecyclerView.visibility =
            if (binding.videoTitlesRecyclerView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    // Set the state and click listener of the favorite button
    private fun setFavoriteButton(isFavorite: Boolean, movie: Movie) {
        val favoriteButtonIcon =
            if (isFavorite) R.drawable.ic_fav_filled else R.drawable.ic_fav_border
        binding.favoriteButton.setImageResource(favoriteButtonIcon)

        binding.favoriteButton.setOnClickListener {
            val message: String = if (isFavorite) getString(R.string.removed_from_favorites)
            else getString(R.string.added_to_favorites)

            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

            viewModel.onFavoriteButtonClicked(movie)
            val newFavoriteState = !isFavorite
            setFavoriteButton(newFavoriteState, movie)
        }
    }

    // Set the state and click listener of the watch list button
    private fun setWatchListButton(isInWatchList: Boolean, movie: Movie) {
        val watchListButtonIcon =
            if (isInWatchList) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_border
        binding.watchListButton.setImageResource(watchListButtonIcon)

        binding.watchListButton.setOnClickListener {
            val message: String = if (isInWatchList) getString(R.string.removed_from_watch_list)
            else getString(R.string.added_to_watch_list)

            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

            viewModel.onWatchListButtonClicked(movie)
            val newWatchListState = !isInWatchList
            setWatchListButton(newWatchListState, movie)
        }
    }

    // Set up the toolbar with the movie title and navigation click listener
    private fun setToolbar(title: String) {
        binding.toolbar.title = title
        binding.toolbar.setNavigationOnClickListener {
            showBottomNavigationView()
            val navController = Navigation.findNavController(binding.root)
            navController.popBackStack()
        }
    }

    // Hide the bottom navigation view
    private fun hideBottomNavigationView() {
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.visibility = View.GONE
    }

    // Show the bottom navigation view
    private fun showBottomNavigationView() {
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE
    }

    // Convert movie runtime from minutes to hours and minutes format
    private fun convertMinutesToHoursAndMinutes(minutes: Int): String {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return "$hours h $remainingMinutes m"
    }

    companion object {
        private const val ARG_ID = "movieId"
        private const val ARG_TITLE = "title"
    }
}

package com.mrguven.movieshelf.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.navigation.Navigation
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mrguven.movieshelf.R
import com.mrguven.movieshelf.data.model.Movie
import com.mrguven.movieshelf.databinding.ItemGridViewBinding
import com.mrguven.movieshelf.databinding.ItemListViewBinding
import com.mrguven.movieshelf.ui.fragments.DiscoverFragmentDirections
import com.mrguven.movieshelf.utils.Constants
import com.mrguven.movieshelf.utils.FormatDate
import com.mrguven.movieshelf.utils.Payloads

class DiscoverPagedAdapter(
    private val viewTypeLiveData: LiveData<ViewType>,
    private val onFavoriteClickListener: (Movie) -> Unit,
    private val onWatchListClickListener: (Movie) -> Unit
) : PagingDataAdapter<Movie, RecyclerView.ViewHolder>(diffCallback) {

    enum class ViewType { GRID, LIST }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }

            override fun getChangePayload(oldItem: Movie, newItem: Movie): Any? {
                val payload = mutableSetOf<String>()
                if (oldItem.isFavorite != newItem.isFavorite) {
                    payload.add(Payloads.PAYLOAD_FAVORITE)
                }
                if (oldItem.isInWatchList != newItem.isInWatchList) {
                    payload.add(Payloads.PAYLOAD_WATCHLIST)
                }
                return if (payload.isEmpty()) null else payload
            }
        }
    }

    // Inflate the item view and create the ViewHolder based on view type
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.GRID.ordinal -> {
                val binding =
                    ItemGridViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                GridViewHolder(binding)
            }
            ViewType.LIST.ordinal -> {
                val binding =
                    ItemListViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ListViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = getItem(position)
        when (holder) {
            is ListViewHolder -> {
                if (currentItem != null) {
                    holder.bind(currentItem)
                }
            }
            is GridViewHolder -> {
                if (currentItem != null) {
                    holder.bind(currentItem)
                }
            }
        }
    }

    // Return the view type for the given position
    override fun getItemViewType(position: Int) =
        viewTypeLiveData.value?.ordinal ?: ViewType.LIST.ordinal

    // Refresh the adapter
    fun refreshAdapter() {
        refresh()
    }

    // ViewHolder class for list view
    inner class ListViewHolder(private val binding: ItemListViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Bind movie data to the list item view
        fun bind(item: Movie) {
            setDetailsListView(item, binding)
            setFavoriteButton(
                binding.favoriteButton, item.isFavorite, bindingAdapterPosition, itemView.context
            )
            setWatchListButton(
                binding.watchListButton,
                item.isInWatchList,
                bindingAdapterPosition,
                itemView.context
            )
            binding.root.setOnClickListener {
                openDetailsClickListener(item, itemView)
            }
        }
    }

    // ViewHolder class for grid view
    inner class GridViewHolder(private val binding: ItemGridViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Bind movie data to the grid item view
        fun bind(item: Movie) {
            setDetailsGridView(item, binding)
            setFavoriteButton(
                binding.favoriteButton, item.isFavorite, bindingAdapterPosition, itemView.context
            )
            setWatchListButton(
                binding.watchListButton,
                item.isInWatchList,
                bindingAdapterPosition,
                itemView.context
            )
            binding.root.setOnClickListener {
                openDetailsClickListener(item, itemView)
            }
        }
    }

    // Set movie details in the list item view
    private fun setDetailsListView(item: Movie, binding: ItemListViewBinding) {
        val posterUrl = Constants.DETAIL_POSTER_BASE_URL + item.posterPath
        Glide.with(binding.poster).load(posterUrl).into(binding.poster)
        binding.title.text = item.title
        val formattedReleaseDate = item.releaseDate?.let { FormatDate.formatDate(it) }
        binding.releaseDate.text = formattedReleaseDate
        binding.voteAverage.text = String.format("%.1f", item.voteAverage)
    }

    // Set movie details in the grid item view
    private fun setDetailsGridView(item: Movie, binding: ItemGridViewBinding) {
        val posterUrl = Constants.DETAIL_POSTER_BASE_URL + item.posterPath
        Glide.with(binding.poster).load(posterUrl).into(binding.poster)
        binding.title.text = item.title
        val formattedReleaseDate = item.releaseDate?.let { FormatDate.formatDate(it) }
        binding.releaseDate.text = formattedReleaseDate
        binding.voteAverage.text = String.format("%.1f", item.voteAverage)
    }

    // Handle click events to open movie details
    private fun openDetailsClickListener(item: Movie, itemView: View) {
        val navController = Navigation.findNavController(itemView)
        val action = DiscoverFragmentDirections.actionDiscoverFragmentToDetailsFragment(
            item.id.toString(), item.title
        )
        navController.navigate(action)
    }

    // Set the favorite button state and click listener
    private fun setFavoriteButton(
        button: ImageView, isFavorite: Boolean, position: Int, context: Context
    ) {
        val favoriteButtonIcon =
            if (isFavorite) R.drawable.ic_fav_filled else R.drawable.ic_fav_border
        button.setImageResource(favoriteButtonIcon)

        button.setOnClickListener {
            val message: String = if (isFavorite) context.getString(R.string.removed_from_favorites)
            else context.getString(R.string.added_to_favorites)

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

            getItem(position)?.let { it1 -> onFavoriteClickListener(it1) }
            setFavoriteButton(button, !isFavorite, position, context)
        }
    }

    // Set the watch list button state and click listener
    private fun setWatchListButton(
        button: ImageView, isInWatchList: Boolean, position: Int, context: Context
    ) {
        val watchListButtonIcon =
            if (isInWatchList) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_border
        button.setImageResource(watchListButtonIcon)

        button.setOnClickListener {
            val message: String =
                if (isInWatchList) context.getString(R.string.removed_from_watch_list)
                else context.getString(R.string.added_to_watch_list)

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

            getItem(position)?.let { it1 -> onWatchListClickListener(it1) }
            setWatchListButton(button, !isInWatchList, position, context)
        }
    }
}

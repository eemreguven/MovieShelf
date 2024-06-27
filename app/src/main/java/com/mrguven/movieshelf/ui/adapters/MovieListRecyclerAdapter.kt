package com.mrguven.movieshelf.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mrguven.movieshelf.R
import com.mrguven.movieshelf.data.local.MovieEntity
import com.mrguven.movieshelf.databinding.ItemListViewBinding
import com.mrguven.movieshelf.utils.Constants
import com.mrguven.movieshelf.utils.FormatDate

class MovieListRecyclerAdapter(
    private val onItemClickListener: (MovieEntity) -> Unit,
    private val onFavoriteClickListener: (MovieEntity) -> Unit,
    private val onWatchListClickListener: (MovieEntity) -> Unit
) : RecyclerView.Adapter<MovieListRecyclerAdapter.ListViewHolder>() {

    private var movieList: ArrayList<MovieEntity> = arrayListOf()

    // Inflate the item view and create the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemListViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(movieList[position])
    }

    // Return the total number of items
    override fun getItemCount() = movieList.size

    // ViewHolder class to hold item view
    inner class ListViewHolder(private val binding: ItemListViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Bind movie data to the item view
        fun bind(item: MovieEntity) {
            setDetailsListView(item, binding)
            setFavoriteButton(binding.favoriteButton, item, bindingAdapterPosition, itemView)
            setWatchListButton(binding.watchListButton, item, bindingAdapterPosition, itemView)

            binding.root.setOnClickListener {
                onItemClickListener(item)
            }
        }
    }

    // Set movie details in the list item view
    private fun setDetailsListView(item: MovieEntity, binding: ItemListViewBinding) {
        val posterUrl = Constants.DETAIL_POSTER_BASE_URL + item.posterPath
        Glide.with(binding.poster).load(posterUrl).into(binding.poster)
        binding.title.text = item.title
        val formattedReleaseDate = item.releaseDate.let { FormatDate.formatDate(it) }
        binding.releaseDate.text = formattedReleaseDate
        binding.voteAverage.text = String.format("%.1f", item.voteAverage)
    }

    // Set the favorite button state and click listener
    private fun setFavoriteButton(
        button: ImageView, item: MovieEntity, position: Int, itemView: View
    ) {
        val favoriteButtonIcon =
            if (item.isFavorite) R.drawable.ic_fav_filled else R.drawable.ic_fav_border
        button.setImageResource(favoriteButtonIcon)

        button.setOnClickListener {
            onFavoriteClickListener(item)
            notifyItemChanged(position)

            val message: String =
                if (item.isFavorite) itemView.context.getString(R.string.removed_from_favorites)
                else itemView.context.getString(R.string.added_to_favorites)
            Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Set the watch list button state and click listener
    private fun setWatchListButton(
        button: ImageView, item: MovieEntity, position: Int, itemView: View
    ) {
        val watchListButtonIcon =
            if (item.isInWatchList) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_border
        button.setImageResource(watchListButtonIcon)

        button.setOnClickListener {
            onWatchListClickListener(item)
            notifyItemChanged(position)

            val message: String =
                if (item.isInWatchList) itemView.context.getString(R.string.removed_from_watch_list)
                else itemView.context.getString(R.string.added_to_watch_list)
            Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Update the list of movies in the adapter
    fun updateList(mList: List<MovieEntity>?) {
        if (mList == null) {
            movieList.clear()
        } else if (mList !== movieList) {
            movieList.clear()
            movieList.addAll(mList)
        }
        notifyDataSetChanged()
    }
}

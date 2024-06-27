package com.mrguven.movieshelf.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mrguven.movieshelf.R
import com.mrguven.movieshelf.data.model.Review
import com.mrguven.movieshelf.databinding.ItemReviewBinding

class ReviewRecyclerAdapter(private val reviewList: List<Review>) :
    RecyclerView.Adapter<ReviewRecyclerAdapter.ReviewViewHolder>() {

    // Inflate the item view and create the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemReviewBinding.inflate(layoutInflater, parent, false)
        return ReviewViewHolder(binding)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviewList[position])
    }

    // Return the total number of items
    override fun getItemCount(): Int {
        return reviewList.size
    }

    // ViewHolder class to hold item view
    class ReviewViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Bind review data to the item view
        fun bind(review: Review) {
            binding.author.text = review.author

            val maxContentLength = 200
            val content = review.content

            // Check if content is longer than the maximum allowed length
            if (content != null && content.length > maxContentLength) {
                // Set truncated content with ellipsis at the end
                val truncatedContent = "${content.substring(0, maxContentLength - 3)}..."
                binding.content.text = truncatedContent
                binding.seeMoreButton.visibility = View.VISIBLE
                binding.seeMoreButton.text = itemView.context.getString(R.string.see_more)
            } else {
                // Display the full content since it's within the limit
                binding.content.text = content
                binding.seeMoreButton.visibility = View.GONE
            }

            // Toggle content between truncated and full version
            binding.seeMoreButton.setOnClickListener {
                if (binding.content.text.length <= maxContentLength) {
                    // Expand the content to show the full text
                    binding.content.text = review.content
                    binding.seeMoreButton.text = itemView.context.getString(R.string.see_less)
                } else {
                    // Collapse the content to show the truncated version
                    val truncatedContent = "${content?.substring(0, maxContentLength - 3)}..."
                    binding.content.text = truncatedContent
                    binding.seeMoreButton.text = itemView.context.getString(R.string.see_more)
                }
            }
        }
    }
}

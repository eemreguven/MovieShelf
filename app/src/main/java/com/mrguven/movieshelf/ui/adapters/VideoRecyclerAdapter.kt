package com.mrguven.movieshelf.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mrguven.movieshelf.R
import com.mrguven.movieshelf.data.model.Video

class VideoRecyclerAdapter(
    private val textList: List<Video>,
    var onItemClickListener: ((videoId: String) -> Unit)? = null
) : RecyclerView.Adapter<VideoRecyclerAdapter.TextViewHolder>() {

    private var lastSelectedPosition = 0

    // Inflate the item view and create the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_text, parent, false)
        return TextViewHolder(itemView)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        val video = textList[position]
        holder.textView.text = video.name

        val isSelected = position == lastSelectedPosition

        val color = if (isSelected) {
            ContextCompat.getColor(holder.itemView.context, R.color.white)
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.oxford_blue)
        }
        holder.textView.setTextColor(color)

        holder.textView.setOnClickListener {
            onItemClickListener?.invoke(video.key ?: "")
            updateSelection(position)
        }
    }

    // Return the total number of items
    override fun getItemCount(): Int {
        return textList.size
    }

    // ViewHolder class to hold item view
    inner class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textView)
    }

    // Update the selection state and notify changes
    private fun updateSelection(newSelectedPosition: Int) {
        val previousSelectedPosition = lastSelectedPosition
        lastSelectedPosition = newSelectedPosition

        if (previousSelectedPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousSelectedPosition)
        }
        if (newSelectedPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(newSelectedPosition)
        }
    }
}

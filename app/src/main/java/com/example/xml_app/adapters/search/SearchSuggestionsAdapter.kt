package com.example.xml_app.adapters.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.databinding.ItemSearchSuggestionRowBinding

class SearchSuggestionsAdapter(
    private val onSuggestionsClick: (String) -> Unit
) : ListAdapter<String, SearchSuggestionsAdapter.ViewHolder>(DiffCallback) {
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
            override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemSearchSuggestionRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val suggestion = getItem(position)
        with(holder.binding) {
            root.setOnClickListener {
                onSuggestionsClick(suggestion)
            }
            tvSearchSuggestion.text = suggestion
        }
    }

    class ViewHolder(val binding: ItemSearchSuggestionRowBinding) : RecyclerView.ViewHolder(binding.root)
}
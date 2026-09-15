package com.example.xml_app.adapters.search

import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.databinding.ItemSearchSuggestionRowBinding

class SearchSuggestionsAdapter(
    private var query: String = "",
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
            tvSearchSuggestion.text = highlightQuery(
                suggestion,
                query
            )
        }
    }

    fun updateQuery(query: String) {
        this.query = query
        notifyItemChanged(0, itemCount)
    }

    private fun highlightQuery(
        suggestion: String,
        query: String
    ): SpannableString {
        val spannable = SpannableString(suggestion)
        if (query.isBlank()) return spannable

        val start = suggestion.indexOf(
            query,
            ignoreCase = true
        )

        if (start >= 0) {
            spannable.setSpan(
                StyleSpan(android.graphics.Typeface.BOLD),
                start,
                start + query.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return spannable
    }

    class ViewHolder(val binding: ItemSearchSuggestionRowBinding) : RecyclerView.ViewHolder(binding.root)
}
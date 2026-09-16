package com.example.xml_app.adapters.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.databinding.ItemSearchHistoryChipBinding
import com.example.xml_app.entities.SearchHistory

class SearchHistoryAdapter(
    val onSearchHistoryClick: (String) -> Unit,
    val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<SearchHistory>() {
        override fun areItemsTheSame(
            oldItem: SearchHistory,
            newItem: SearchHistory
        ) = oldItem.uid == newItem.uid

        override fun areContentsTheSame(
            oldItem: SearchHistory,
            newItem: SearchHistory
        ) = oldItem == newItem

    }
    private val differ = AsyncListDiffer(this, diffCallback)

    var searchHistory: List<SearchHistory>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemSearchHistoryChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = searchHistory[position]
        with(holder.binding) {
            tvSearchHistory.text = item.query

            root.setOnClickListener {
                onSearchHistoryClick(item.query)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(item.uid)
            }
        }
    }

    override fun getItemCount() = searchHistory.size

    class ViewHolder(val binding: ItemSearchHistoryChipBinding) : RecyclerView.ViewHolder(binding.root)
}
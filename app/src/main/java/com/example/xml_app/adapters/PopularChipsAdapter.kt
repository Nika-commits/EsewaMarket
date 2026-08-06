package com.example.xml_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.databinding.MostPopularBadgeBinding

class PopularChipsAdapter(
    val onClick: (String) -> Unit
) : RecyclerView.Adapter<PopularChipsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            MostPopularBadgeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var item: List<String>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val category = item[position]
        with(holder.binding) {
            root.text = category
            root.setOnClickListener {
                onClick(category)
            }
        }

    }

    override fun getItemCount(): Int {
        return item.size
    }

    class ViewHolder(val binding: MostPopularBadgeBinding) : RecyclerView.ViewHolder(binding.root)
}

package com.example.xml_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.databinding.LayoutSizeSelectorBinding

class SizeSelectorAdapter(
    val onSizeChange: (String) -> Unit
) : RecyclerView.Adapter<SizeSelectorAdapter.ViewHolder>() {

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

    var sizes: List<String>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            LayoutSizeSelectorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.apply {
            val size = sizes[position]
            binding.btnSize.text = size

            binding.btnSize.setOnClickListener {
                onSizeChange(size)
            }
        }
    }

    override fun getItemCount(): Int {
        return sizes.size
    }

    class ViewHolder(val binding: LayoutSizeSelectorBinding) :
        RecyclerView.ViewHolder(binding.root)
}
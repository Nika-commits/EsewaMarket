package com.example.xml_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.xml_app.R
import com.example.xml_app.databinding.ItemCarouselImageBinding

class ProductCarouselAdapter(
    val onImageClick: (String) -> Unit
) : RecyclerView.Adapter<ProductCarouselAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemCarouselImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var imageUrls: List<String>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val currentImage = imageUrls[position]
        holder.apply {
            Glide.with(holder.itemView.context)
                .load(currentImage)
                .placeholder(R.drawable.tshirt)
                .into(holder.binding.ivProductCarouselImage)
        }
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }

    inner class ViewHolder(val binding: ItemCarouselImageBinding) :
        RecyclerView.ViewHolder(binding.root)
}
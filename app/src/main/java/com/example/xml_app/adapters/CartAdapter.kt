package com.example.xml_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.xml_app.R
import com.example.xml_app.databinding.ItemCartProductBinding
import com.example.xml_app.models.Product

class CartAdapter : RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemCartProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
            return oldItem.id == newItem.id
        }

    }

    private val differ = AsyncListDiffer(this, differCallback)

    var products: List<Product>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val currentProduct = products[position]

        holder.apply {
            Glide.with(holder.itemView.context)
                .load(currentProduct.imageUrls[0])
                .placeholder(R.drawable.ic_more)
                .into(holder.binding.ivProductImage)

            binding.tvProductPrice.text = currentProduct.price.toString()
            binding.tvProductName.text = currentProduct.name
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    class ViewHolder(val binding: ItemCartProductBinding) : RecyclerView.ViewHolder(binding.root)
}
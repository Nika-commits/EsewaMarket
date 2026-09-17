package com.example.xml_app.adapters.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.xml_app.databinding.ItemPopularProductBinding
import com.example.xml_app.models.Product

class SearchMostPopularProductAdapter(
    val onProductClick: (Int) -> Unit
) : RecyclerView.Adapter<SearchMostPopularProductAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemPopularProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(
            oldItem: Product,
            newItem: Product
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: Product,
            newItem: Product
        ) = oldItem == newItem
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var products: List<Product>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = products[position]
        with(holder.binding) {
            tvName.text = item.name
            tvBrand.text = item.brand.uppercase()
            tvPrice.text = item.price.toFloat().toString()

            Glide.with(root)
                .load(item.imageUrls.firstOrNull())
                .into(ivProductImage)

            root.setOnClickListener {
                onProductClick(item.id)
            }
        }
    }

    override fun getItemCount() = products.size

    class ViewHolder(val binding: ItemPopularProductBinding) : RecyclerView.ViewHolder(binding.root)
}
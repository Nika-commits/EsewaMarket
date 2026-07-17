package com.example.xml_app.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.xml_app.Models.Product
import com.example.xml_app.R
import com.example.xml_app.databinding.ItemProductBinding

//import com.example.xml_app.databinding.ItemProductCardBinding

class FeaturedProductsAdapter(
    val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<FeaturedProductsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var products: List<Product>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.apply {
            val product = products[position]
            binding.tvProductName.text = product.name
//            binding.ivProductImage.setImageResource(product.imageUrl)
            binding.tvPrice.text = product.price.toString()
            binding.tvProductStatus.text = product.status

            binding.root.setOnClickListener {
                onProductClick(product)
            }

            Glide.with(itemView)
                .load(product.imageUrls[0])
                .placeholder(R.drawable.tshirt)
                .error(R.drawable.resource_default)
                .into(binding.ivProductImage)
        }


    }

    override fun getItemCount(): Int {
        return products.size
    }

}
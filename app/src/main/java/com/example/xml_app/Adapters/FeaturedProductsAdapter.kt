package com.example.xml_app.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.Models.Product
import com.example.xml_app.R
import com.example.xml_app.databinding.ItemIndicatorBinding
import com.example.xml_app.databinding.ItemProductBinding

//import com.example.xml_app.databinding.ItemProductCardBinding

class FeaturedProductsAdapter(
    val featuredProducts: List<Product>
) : RecyclerView.Adapter<FeaturedProductsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)


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
        val product = featuredProducts[position]
        holder.apply {
            binding.tvProductName.text = product.name
            binding.ivProductImage.setImageResource(product.imageUrl)
            binding.tvPrice.text = product.price.toString()
            binding.tvProductStatus.text = product.status
        }
    }

    override fun getItemCount(): Int {
        return featuredProducts.size
    }

}
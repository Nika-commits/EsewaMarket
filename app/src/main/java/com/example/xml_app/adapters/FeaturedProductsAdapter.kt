package com.example.xml_app.adapters

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.xml_app.R
import com.example.xml_app.databinding.ItemProductBinding
import com.example.xml_app.models.Product
import com.example.xml_app.models.ProductState

//import com.example.xml_app.databinding.ItemProductCardBinding

class FeaturedProductsAdapter(
    val onProductClick: (Product) -> Unit,
    val onFavouriteClick: (Product) -> Unit,
    val onCartIncrement: (Product) -> Unit,
    val onCartDecrement: (Product) -> Unit
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

    var productStates: Map<Int, ProductState> = emptyMap()

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
            val state = productStates[product.id] ?: ProductState()
            Log.d("Adapter", "Product ${product.id} -> $state")
            if (state.isFavourite) {
                binding.ibFavourites.setImageResource(R.drawable.ic_filled_favourite)
                binding.ibFavourites.imageTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.primaryGreen
                        )
                    )
            } else {
                binding.ibFavourites.setImageResource(R.drawable.ic_fav)
                binding.ibFavourites.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.lightGrey
                    )
                )
            }

            if (state.cartCount > 0) {
                binding.ibAddToCart.visibility = View.GONE
                binding.llCartCountStepper.visibility = View.VISIBLE
                binding.tvCartCount.text = state.cartCount.toString()
            } else {
                binding.llCartCountStepper.visibility = View.GONE
                binding.ibAddToCart.visibility = View.VISIBLE
            }

            binding.tvProductName.text = product.name
            binding.tvPrice.text = product.price.toString()
            binding.tvProductStatus.text = product.status
            binding.tvProductBrand.text = product.brand
            
            binding.root.setOnClickListener {
                onProductClick(product)
            }

            binding.ibFavourites.setOnClickListener {
                onFavouriteClick(product)
            }

            binding.ibAddToCart.setOnClickListener {
                onCartIncrement(product)
            }

            binding.ibCartDecrement.setOnClickListener {
                onCartDecrement(product)
            }

            binding.ibCartIncrement.setOnClickListener {
                onCartIncrement(product)
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
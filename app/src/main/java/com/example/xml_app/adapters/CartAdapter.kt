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
import com.example.xml_app.models.ProductState

class CartAdapter(
    val onProductClick: (Int?) -> Unit,
    val onCartIncrement: (Int?) -> Unit,
    val onCartDecrement: (Int?) -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {
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
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, differCallback)
    var products: List<Product?>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    var productStates: Map<Int, ProductState> = emptyMap()


    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val currentProduct = products[position]
        val state = productStates[currentProduct?.id] ?: ProductState()
        val totalPrice = currentProduct?.price?.times(state.cartCount)

        holder.apply {
            Glide.with(holder.itemView.context)
                .load(currentProduct?.imageUrls[0])
                .placeholder(R.drawable.ic_more)
                .into(holder.binding.ivProductImage)

            binding.tvProductPrice.text = totalPrice.toString()
            binding.tvProductName.text = currentProduct?.name
            binding.tvProductBrand.text = currentProduct?.brand

            binding.tvCartCount.text = state.cartCount.toString()

            binding.root.setOnClickListener {
                onProductClick(currentProduct?.id)
            }

            binding.btnDecrementCart.setOnClickListener {
                onCartDecrement(currentProduct?.id)
            }

            binding.btnIncrementCart.setOnClickListener {
                onCartIncrement(currentProduct?.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    class ViewHolder(val binding: ItemCartProductBinding) : RecyclerView.ViewHolder(binding.root)
}
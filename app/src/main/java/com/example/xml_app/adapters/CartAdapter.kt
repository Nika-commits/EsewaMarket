package com.example.xml_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.xml_app.R
import com.example.xml_app.databinding.ItemCartProductBinding
import com.example.xml_app.models.ProductUiModel

class CartAdapter(
    val onProductClick: (Int) -> Unit,
    val onCartIncrement: (Int) -> Unit,
    val onCartDecrement: (Int, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemCartProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    private val differCallback = object : DiffUtil.ItemCallback<ProductUiModel>() {
        override fun areItemsTheSame(
            oldItem: ProductUiModel,
            newItem: ProductUiModel
        ): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(
            oldItem: ProductUiModel,
            newItem: ProductUiModel
        ): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, differCallback)
    var products: List<ProductUiModel>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

//    var productStates: Map<Int, ProductState> = emptyMap()


    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = products[position]
        val product = item.product
        val totalPrice = product.price.times(item.cartCount)

        with(holder.binding) {

            Glide.with(root)
                .load(product.imageUrls.firstOrNull())
                .placeholder(R.drawable.resource_default)
                .error(R.drawable.resource_default)
                .into(ivProductImage)

            tvProductName.text = product.name
            tvProductBrand.text = product.brand
            tvProductPrice.text = product.price.toString()
            tvCartCount.text = item.cartCount.toString()

            root.setOnClickListener {
                onProductClick(product.id)
            }

            btnDecrementCart.setOnClickListener {
                onCartDecrement(product.id, item.cartCount)
            }

            btnIncrementCart.setOnClickListener {
                onCartIncrement(product.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    class ViewHolder(val binding: ItemCartProductBinding) : RecyclerView.ViewHolder(binding.root)
}
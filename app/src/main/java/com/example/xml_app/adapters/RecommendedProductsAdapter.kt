package com.example.xml_app.adapters


import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.xml_app.R
import com.example.xml_app.databinding.ItemProductBinding
import com.example.xml_app.models.Product
import com.example.xml_app.models.ProductUiModel

class RecommendedProductsAdapter(
    private val onProductClick: (Product) -> Unit,
    private val onFavouriteClick: (Product) -> Unit,
    private val onCartIncrement: (Product) -> Unit,
    private val onCartDecrement: (Product) -> Unit
) : PagingDataAdapter<ProductUiModel, RecommendedProductsAdapter.ViewHolder>(ProductDiffCallback) {
    companion object {
        private val ProductDiffCallback = object : DiffUtil.ItemCallback<ProductUiModel>() {
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
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = getItem(position) ?: return
        val product = item.product

        with(holder.binding) {
            if (item.isFavourite) {
                ibFavourites.setImageResource(R.drawable.ic_filled_favourite)
                ibFavourites.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        root.context,
                        R.color.primaryGreen
                    )
                )
            } else {
                ibFavourites.setImageResource(R.drawable.ic_fav)
                ibFavourites.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.lightGrey))
            }
            if (item.cartCount > 0) {
                ibAddToCart.visibility = View.GONE
                llCartCountStepper.visibility = View.VISIBLE

                tvCartCount.text = item.cartCount.toString()

            } else {
                llCartCountStepper.visibility = View.GONE
                ibAddToCart.visibility = View.VISIBLE
            }

            tvProductName.text = product.name
            tvPrice.text = product.price.toString()
            tvProductStatus.text = product.status
            tvProductBrand.text = product.brand

            Glide.with(root)
                .load(product.imageUrls.firstOrNull())
                .placeholder(R.drawable.resource_default)
                .error(R.drawable.resource_default)
                .into(ivProductImage)

            root.setOnClickListener {
                onProductClick(product)
            }

            ibFavourites.setOnClickListener {
                onFavouriteClick(product)
            }

            ibAddToCart.setOnClickListener {
                onCartIncrement(product)
            }

            ibCartIncrement.setOnClickListener {
                onCartIncrement(product)
            }

            ibCartDecrement.setOnClickListener {
                onCartDecrement(product)
            }
        }
    }

    class ViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)
}
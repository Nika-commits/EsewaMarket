package com.example.xml_app.adapters

import android.content.res.ColorStateList
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
import com.example.xml_app.models.ProductUiModel

class ProductsAdapter(
    val onProductClick: (Product) -> Unit,
    val onFavouriteClick: (Product, Boolean) -> Unit,
    val onCartIncrement: (Product, Int?) -> Unit,
    val onCartDecrement: (Product, Int) -> Unit
) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        var hasAnimated = false
    }

    private val diffCallback = object : DiffUtil.ItemCallback<ProductUiModel>() {
        override fun areItemsTheSame(oldItem: ProductUiModel, newItem: ProductUiModel): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: ProductUiModel, newItem: ProductUiModel): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var products: List<ProductUiModel>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    var isLoading = false
        set(value) {
            if (field == value) return
            field = value
            if (value) {
                notifyItemInserted(products.size)
            } else {
                notifyItemRemoved(products.size)
            }
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
        val item = products[position]
        val product = item.product

        with(holder.binding) {
            if (item.isFavourite) {
                ibFavourites.setImageResource(R.drawable.ic_filled_favourite)
                ibFavourites.imageTintList =
                    ColorStateList.valueOf(
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

            root.setOnClickListener {
                onProductClick(product)
            }

            ibFavourites.setOnClickListener {
                onFavouriteClick(product, item.isFavourite)
            }

            ibAddToCart.setOnClickListener {
                onCartIncrement(product, item.cartCount)
            }

            ibCartIncrement.setOnClickListener {
                onCartIncrement(product, null)
            }

            ibCartDecrement.setOnClickListener {
                onCartDecrement(product, item.cartCount)
            }

            Glide.with(root)
                .load(product.imageUrls.firstOrNull())
                .into(ivProductImage)

            if (!holder.hasAnimated) {
                holder.hasAnimated = true
                holder.itemView.apply {
                    alpha = 0f
                    animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start()
                }
            }
        }
    }

    private fun showCartStepper(binding: ItemProductBinding) {
        binding.ibAddToCart.animate().cancel()
        binding.llCartCountStepper.animate().cancel()

        binding.ibAddToCart.animate()
            .alpha(0f)
            .scaleX(0.7f)
            .scaleY(0.7f)
            .setDuration(150)
            .withEndAction {
                binding.ibAddToCart.visibility = View.GONE

                binding.llCartCountStepper.apply {
                    visibility = View.VISIBLE
                    alpha = 0f
                    scaleX = 0.7f
                    scaleY = 0.7f
                }
                binding.llCartCountStepper.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .start()
            }
            .start()
    }

    private fun showAddToCartButton(binding: ItemProductBinding) {
        binding.llCartCountStepper.animate().cancel()
        binding.ibAddToCart.animate().cancel()

        binding.llCartCountStepper.animate()
            .alpha(0f)
            .scaleX(0.7f)
            .scaleY(0.7f)
            .setDuration(150)
            .withEndAction {
                binding.llCartCountStepper.visibility = View.GONE

                binding.ibAddToCart.apply {
                    visibility = View.VISIBLE
                }
            }

    }

    override fun getItemCount() = products.size
}
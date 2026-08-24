package com.example.xml_app.adapters.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.adapters.CartAdapter
import com.example.xml_app.databinding.SectionCartCartItemsBinding

class CartCartItemsAdapter(
    val cartAdapter: CartAdapter,
    private val onContinueShopping: () -> Unit
) : RecyclerView.Adapter<CartCartItemsAdapter.ViewHolder>() {
    private var isEmpty = true

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            SectionCartCartItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(
            binding,
            cartAdapter,
            onContinueShopping
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(isEmpty)
    }

    override fun getItemCount(): Int = 1

    fun setEmpty(empty: Boolean) {
        if (isEmpty == empty) return
        isEmpty = empty
        notifyItemChanged(0)
    }

    class ViewHolder(
        private val binding: SectionCartCartItemsBinding,
        cartAdapter: CartAdapter,
        onContinueShopping: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.rvCartProducts.apply {
                adapter = cartAdapter
                layoutManager = LinearLayoutManager(context)
                itemAnimator = null
                isNestedScrollingEnabled = false
            }
            binding.emptyCart.btnContinueShopping.setOnClickListener {
                onContinueShopping()
            }
        }

        fun bind(isEmpty: Boolean) {
            binding.emptyCart.root.isVisible = isEmpty
            binding.rvCartProducts.isVisible = !isEmpty
        }
    }
}
package com.example.xml_app.adapters.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.R
import com.example.xml_app.adapters.ProductsAdapter
import com.example.xml_app.databinding.SectionHomeProductsBinding
import com.example.xml_app.utils.SpacingItemDecoration

class HomeHotDealsAdapter(
    private val productsAdapter: ProductsAdapter,
    private val onSeeAllClick: () -> Unit

) : RecyclerView.Adapter<HomeHotDealsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            SectionHomeProductsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, productsAdapter)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        with(holder.binding) {
            featuredProducts.tvHeaderTitle.text = "Hot Deals of the day"
            featuredProducts.ibHeaderButton.setOnClickListener { onSeeAllClick() }
        }
    }

    override fun getItemCount(): Int = 1

    class ViewHolder(
        val binding: SectionHomeProductsBinding,
        productsAdapter: ProductsAdapter
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.rvFeaturedProducts.apply {
                layoutManager = GridLayoutManager(
                    context,
                    2
                )
                adapter = productsAdapter
                itemAnimator = null
                addItemDecoration(
                    SpacingItemDecoration(
                        2,
                        context.resources.getDimensionPixelSize(R.dimen.spacing_medium)
                    )
                )
            }
        }
    }
}
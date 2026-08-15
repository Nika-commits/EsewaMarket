package com.example.xml_app.adapters.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.R
import com.example.xml_app.adapters.ProductsAdapter
import com.example.xml_app.databinding.SectionHomeProductsBinding
import com.example.xml_app.utils.HorizontalItemDecoration

class HomeFeaturedAdapter(
    private val productsAdapter: ProductsAdapter,
    private val onSeeAllClick: () -> Unit

) : RecyclerView.Adapter<HomeFeaturedAdapter.ViewHolder>() {
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
            featuredProducts.tvHeaderTitle.text = "Featured Products"
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
                layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = productsAdapter
                addItemDecoration(
                    HorizontalItemDecoration(
                        context.resources.getDimensionPixelSize(R.dimen.spacing_medium)
                    )
                )
            }
        }
    }
}
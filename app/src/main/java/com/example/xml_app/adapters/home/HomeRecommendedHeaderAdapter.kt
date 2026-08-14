package com.example.xml_app.adapters.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.databinding.ItemRecommendedProductsHeaderBinding

class HomeRecommendedHeaderAdapter(
    private val onSeeAllClick: () -> Unit
) : RecyclerView.Adapter<HomeRecommendedHeaderAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemRecommendedProductsHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        with(holder.binding.recommendedHeader) {
            tvHeaderTitle.text = "Recommended Products"
            ibHeaderButton.setOnClickListener { onSeeAllClick() }
        }
    }

    override fun getItemCount(): Int = 1

    class ViewHolder(val binding: ItemRecommendedProductsHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)
}
package com.example.xml_app.adapters.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.R
import com.example.xml_app.adapters.RecommendedProductsAdapter
import com.example.xml_app.databinding.SectionRecommendedProductsBinding
import com.example.xml_app.utils.SpacingItemDecoration

class HomeRecommendedAdapter(
    private val recommendedAdapter: RecommendedProductsAdapter,
    val onSeeAllClick: () -> Unit
) : RecyclerView.Adapter<HomeRecommendedAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = SectionRecommendedProductsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    private var isLoading = false
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        with(holder.binding) {
            recommendedHeader.tvHeaderTitle.text = "Recommended Products"
            recommendedHeader.ibHeaderButton.setOnClickListener { onSeeAllClick() }

            rvRecommendedProducts.apply {
                layoutManager = GridLayoutManager(context, 2)
                adapter = recommendedAdapter
                itemAnimator = null

                if (itemDecorationCount == 0) {
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

    fun setLoading(loading: Boolean) {
        if (isLoading == loading) return

        isLoading = loading
        notifyItemChanged(0)
    }

    override fun getItemCount(): Int = 1

    class ViewHolder(val binding: SectionRecommendedProductsBinding) :
        RecyclerView.ViewHolder(binding.root)
}
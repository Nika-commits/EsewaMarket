package com.example.xml_app.adapters.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.adapters.PopularChipsAdapter
import com.example.xml_app.databinding.ItemHomeMostPopularBinding
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class HomeMostPopularAdapter(
    private val chipsAdapter: PopularChipsAdapter,
    private val onClick: () -> Unit,
) : RecyclerView.Adapter<HomeMostPopularAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemHomeMostPopularBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        with(holder.binding) {
            mostPopularHeader.tvHeaderTitle.text = "Most Popular"
            rvMostPopular.apply {
                layoutManager = FlexboxLayoutManager(context).apply {
                    flexDirection = com.google.android.flexbox.FlexDirection.ROW
                    flexWrap = FlexWrap.WRAP
                    justifyContent = JustifyContent.FLEX_START
                }
                adapter = chipsAdapter
            }
        }
    }

    override fun getItemCount(): Int = 1

    class ViewHolder(val binding: ItemHomeMostPopularBinding) :
        RecyclerView.ViewHolder(binding.root)
}
package com.example.xml_app.adapters.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.databinding.ItemHomeBannerBinding

class HomeBannerAdapter(
    val onClick: () -> Unit = {}
) : RecyclerView.Adapter<HomeBannerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemHomeBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        with(holder.binding) {
            root.setOnClickListener { onClick() }
        }
    }

    override fun getItemCount(): Int = 1

    class ViewHolder(val binding: ItemHomeBannerBinding) : RecyclerView.ViewHolder(binding.root)
}
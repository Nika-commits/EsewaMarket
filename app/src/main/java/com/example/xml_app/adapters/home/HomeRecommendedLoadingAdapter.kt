package com.example.xml_app.adapters.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.databinding.ItemLoadingBinding

class HomeRecommendedLoadingAdapter :
    RecyclerView.Adapter<HomeRecommendedLoadingAdapter.ViewHolder>() {

    private var isLoading = false
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) = Unit


    override fun getItemCount(): Int {
        return if (isLoading) 1 else 0
    }

    fun setLoading(loading: Boolean) {
        if (isLoading == loading) return
        isLoading = loading
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root)
}
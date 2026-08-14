package com.example.xml_app.adapters.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.adapters.HeroViewPagerAdapter
import com.example.xml_app.databinding.ItemHomeHeaderBinding
import com.example.xml_app.models.Hero
import com.google.android.material.tabs.TabLayoutMediator

class HomeHeadAdapter(
    private val userName: String = "Pranish ,",
    private val heroes: List<Hero>,
    private val onFilterClick: () -> Unit,
    private val onToolbarReady: (Toolbar) -> Unit

) : RecyclerView.Adapter<HomeHeadAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemHomeHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        with(holder.binding) {
            tvUsername.text = userName
            heroViewPager.adapter = HeroViewPagerAdapter(heroes.toMutableList())
            TabLayoutMediator(
                heroIndicator,
                heroViewPager
            ) { tab, _ ->
                tab.setCustomView(com.example.xml_app.R.layout.item_indicator)
            }.attach()

            searchBox.setEndIconOnClickListener {
                onFilterClick()
            }

            onToolbarReady(toolbar)
        }
    }

    override fun getItemCount(): Int = 1
    class ViewHolder(val binding: ItemHomeHeaderBinding) : RecyclerView.ViewHolder(binding.root)
}
package com.example.xml_app.adapters.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.R
import com.example.xml_app.adapters.HeroViewPagerAdapter
import com.example.xml_app.databinding.ItemHomeHeaderBinding
import com.example.xml_app.models.Hero
import com.google.android.material.tabs.TabLayoutMediator

class HomeHeadAdapter(
    private var userName: String = "Pranish ,",
    private val heroes: List<Hero>,
    private val onFilterClick: () -> Unit,
    private val onSearchClick: () -> Unit,
    private val onToolbarReady: (Toolbar) -> Unit,

    ) : RecyclerView.Adapter<HomeHeadAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemHomeHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(

            binding,
            heroes,
            onFilterClick,
            onSearchClick,
            onToolbarReady,
            userName
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(userName)
    }

    fun setUsername(newUserName: String) {
        if (userName == newUserName) return
        userName = newUserName
        notifyItemChanged(0)
    }

    override fun getItemCount(): Int = 1
    class ViewHolder(
        val binding: ItemHomeHeaderBinding,
        heroes: List<Hero>,
        onFilterClick: () -> Unit,
        onSearchClick: () -> Unit,
        onToolbarReady: (Toolbar) -> Unit,
        username: String
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.tvUsername.text = username
            binding.heroViewPager.adapter = HeroViewPagerAdapter(heroes.toMutableList())
            TabLayoutMediator(
                binding.heroIndicator,
                binding.heroViewPager
            ) { tab, _ ->
                tab.setCustomView(R.layout.item_indicator)
            }.attach()

            binding.searchBox.setEndIconOnClickListener { onFilterClick() }
            binding.searchBox.setOnClickListener { onSearchClick() }
            onToolbarReady(binding.toolbar)
        }

        fun bind(username: String) {
            binding.tvUsername.text = username
        }

    }
}
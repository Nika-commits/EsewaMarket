package com.example.xml_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.R
import com.example.xml_app.models.Hero

class HeroViewPagerAdapter(
    val heroImages: MutableList<Hero>
) : RecyclerView.Adapter<HeroViewPagerAdapter.ViewPagerViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hero, parent, false)
        return ViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewPagerViewHolder,
        position: Int
    ) {
        val currentHero = heroImages[position]
        holder.heroImage.setImageResource(currentHero.imageResId)
        holder.heroImage.contentDescription = currentHero.title
    }

    override fun getItemCount(): Int {
        return heroImages.size
    }

    inner class ViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val heroImage = itemView.findViewById<ImageView>(R.id.ivHeroImage)
    }


}
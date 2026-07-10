package com.example.xml_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class HeroAdapter (
    var heroSection: List<Hero>
) : RecyclerView.Adapter<HeroAdapter.HeroViewHolder>(){
    inner class HeroViewHolder(heroView: View) : RecyclerView.ViewHolder(heroView){
        val heroImage: ImageView = itemView.findViewById(R.id.ivHeroImage)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HeroViewHolder {
        // when recycler view need new holder after user scrolls, needs to create nre items ,create layout for new view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hero_section, parent, false)
        return HeroViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: HeroViewHolder,
        position: Int
    ) {
        val hero = heroSection[position]
        holder.heroImage.contentDescription = hero.title
        holder.heroImage.setImageResource(hero.imageResId)
    }

    override fun getItemCount(): Int {
        // gets the numbers of items we have in recycler view
        // don't write 0 -> returns empty list, return heroSecction.size
        return heroSection.size
    }


}
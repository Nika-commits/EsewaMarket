package com.example.xml_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryRecyclerViewAdapter(
    val categories: List<Category>
): RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_box, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val category = categories[position]
        holder.categoryName.text = category.categoryName
        holder.icon.setImageResource(category.categroyIcon)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val icon = itemView.findViewById<ImageView>(R.id.ivCategoryIcon)
        val categoryName = itemView.findViewById<TextView>(R.id.tvCategoryName)
    }
}
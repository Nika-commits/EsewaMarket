package com.example.xml_app.adapters.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.R
import com.example.xml_app.adapters.CategoryRecyclerViewAdapter
import com.example.xml_app.databinding.SectionCategoriesBinding
import com.example.xml_app.models.Category
import com.example.xml_app.utils.HorizontalItemDecoration

class HomeCategoriesAdapter(
    private val categories: List<Category>,
    private val onCategoryClick: (Category) -> Unit,
    private val onSeeAllClick: () -> Unit

) : RecyclerView.Adapter<HomeCategoriesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            SectionCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, categories, onCategoryClick)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        with(holder.binding) {
            categorySection.tvHeaderTitle.text = "Categories"
            categorySection.ibHeaderButton.setOnClickListener { onSeeAllClick() }
        }
    }

    override fun getItemCount(): Int = 1

    class ViewHolder(
        val binding: SectionCategoriesBinding,
        categories: List<Category>,
        onCategoryClick: (Category) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.rvCategoryOptions.apply {
                layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = CategoryRecyclerViewAdapter(categories) {
                    onCategoryClick(it)
                }

                addItemDecoration(
                    HorizontalItemDecoration(
                        context.resources.getDimensionPixelSize(R.dimen.spacing_low)

                    )
                )
            }
        }
    }
}
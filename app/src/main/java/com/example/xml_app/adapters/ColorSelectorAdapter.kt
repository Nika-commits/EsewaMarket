package com.example.xml_app.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.databinding.ItemColorSelectorBinding
import com.example.xml_app.models.Color

class ColorSelectorAdapter(
    val onColorChange: (String) -> Unit
) : RecyclerView.Adapter<ColorSelectorAdapter.ViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Color>() {
        override fun areItemsTheSame(
            oldItem: Color,
            newItem: Color
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: Color,
            newItem: Color
        ): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var colors: List<Color>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemColorSelectorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.apply {
            val color = colors[position]
            binding.btnColorSelector.backgroundTintList =
                ColorStateList.valueOf(android.graphics.Color.parseColor(color.hexCode))
            binding.btnColorSelector.text = color.name

            binding.btnColorSelector.setOnClickListener {
                onColorChange(color.name)
            }
        }
    }

    override fun getItemCount(): Int {
        return colors.size
    }

    class ViewHolder(val binding: ItemColorSelectorBinding) :
        RecyclerView.ViewHolder(binding.root)
}
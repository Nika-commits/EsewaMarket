package com.example.xml_app.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.R
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
            return newItem == oldItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var colors: List<Color>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    var selectedColor: String? = null
        set(value) {
            val old = field
            field = value

            old?.let {
                val index = colors.indexOfFirst { it.name == old }
                if (index != -1) notifyItemChanged(index)
            }

            value?.let {
                val index = colors.indexOfFirst { it.name == value }
                if (index != -1) notifyItemChanged(index)
            }
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
            val isSelected = color.name == selectedColor
            val btn = binding.btnColorSelector

            if (isSelected) {
                btn.icon = ContextCompat.getDrawable(btn.context, R.drawable.ic_tick_check)
            } else {
                btn.icon = null
            }

            binding.btnColorSelector.backgroundTintList =
                ColorStateList.valueOf(color.hexCode.toColorInt())

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
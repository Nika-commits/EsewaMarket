package com.example.xml_app.ui

import android.content.Context
import android.content.res.ColorStateList
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.xml_app.R
import com.google.android.material.button.MaterialButton

class SizeSelectorButton @JvmOverloads constructor(
    val size: String,
    context: Context,
) : MaterialButton(context) {

    init {
        applyDefaultStyle()
    }

    fun applyDefaultStyle() {
        text = size
        textSize = 14.0f
        cornerRadius =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics)
                .toInt()
        strokeWidth = 1
        strokeColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.textDark))
        backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.surface))
        setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)))

        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
    }

    fun applySelectedStyle() {
        backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primaryGreen))
        setTextColor(ContextCompat.getColor(context, R.color.surface))

    }

}
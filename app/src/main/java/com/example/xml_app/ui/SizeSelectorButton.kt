package com.example.xml_app.ui

import android.content.Context
import android.content.res.ColorStateList
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
        cornerRadius = 8
        strokeWidth = 1
        strokeColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.textDark))
        backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.surface))
        setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)))
        width = 32
        height = 44

    }

    fun applySelectedStyle() {
        backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primaryGreen))
        setTextColor(ContextCompat.getColor(context, R.color.surface))

    }

}
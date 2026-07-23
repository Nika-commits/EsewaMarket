package com.example.xml_app.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)

        if (position == RecyclerView.NO_POSITION) return
        outRect.left = if (position == 0) spacing else spacing / 2
        outRect.right = spacing / 2
        outRect.top = spacing
        outRect.bottom = spacing

    }

}
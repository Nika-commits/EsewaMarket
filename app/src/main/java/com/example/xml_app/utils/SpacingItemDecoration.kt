package com.example.xml_app.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecoration(
    private val spacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)

        if (position % 2 == 0) {
            outRect.left = spacing
            outRect.right = spacing / 2
        } else {
            outRect.left = spacing / 2
            outRect.right = spacing
        }
        outRect.bottom = spacing
        outRect.top = spacing
    }

}
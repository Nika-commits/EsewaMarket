package com.example.xml_app.utils

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.example.xml_app.R
import com.google.android.material.snackbar.Snackbar

object CustomSnackbar {

    fun show(
        view: View,
        context: Context,
        text: String,
        anchorView: View? = null,
        actionText: String? = null,
        action: (() -> Unit)? = null
    ) {
        val snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT)
        anchorView?.let { snackbar.setAnchorView(it) }
        snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.black))
        snackbar.setTextColor(ContextCompat.getColor(context, R.color.white))
        snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.primaryGreen))
        snackbar.view.setPadding(
            snackbar.view.paddingStart,
            8,
            snackbar.view.paddingEnd,
            8
        )

        if (actionText != null && action != null) {
            snackbar.setAction(actionText) {
                action()
            }
        }
        snackbar.show()
    }

}
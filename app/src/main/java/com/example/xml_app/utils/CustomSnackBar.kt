package com.example.xml_app.utils

import android.content.Context
import android.view.View
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.xml_app.R
import com.example.xml_app.utils.styles.Black
import com.example.xml_app.utils.styles.PrimaryGreen
import com.example.xml_app.utils.styles.Surface
import com.google.android.material.snackbar.Snackbar

object CustomSnackBar {
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

@Composable
fun CustomComposeSnackBar(
    snackBarData: SnackbarData,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Snackbar(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        containerColor = Black,
        contentColor = Surface,
        actionContentColor = PrimaryGreen,
        action = {
            snackBarData.visuals.actionLabel?.let { actionLabel ->
                Text(actionLabel)
            }
        }
    ) {
        Text(snackBarData.visuals.message)

    }

}
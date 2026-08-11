package com.example.xml_app.utils.custom

import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import com.example.xml_app.R

@Composable
fun ActionIcon(
    onClick: () -> Unit,
    icon: Painter,
    modifier: Modifier = Modifier

) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .background(colorResource(R.color.esewaRed))
    ) {
        Icon(painter = icon, contentDescription = null)
    }

}
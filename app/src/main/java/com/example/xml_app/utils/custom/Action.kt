package com.example.xml_app.utils.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.xml_app.R

@Composable
fun ActionIcon(
    onClick: () -> Unit,
    icon: Painter,
    modifier: Modifier = Modifier

) {
    Box(
        modifier = modifier
            .width(74.dp)
            .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
            .background(colorResource(R.color.textLight300)),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = colorResource(R.color.esewaRed),
                    shape = RoundedCornerShape(32.dp)
                )
        ) {
            Icon(painter = icon, contentDescription = null, tint = Color.White)
        }
    }

}
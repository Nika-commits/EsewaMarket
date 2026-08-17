package com.example.xml_app.utils.styles.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xml_app.R
import com.example.xml_app.utils.SourceSansPro
import com.example.xml_app.utils.styles.PrimaryGreen
import com.example.xml_app.utils.styles.Surface

@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: Int? = null,
    onClick: () -> Unit,
) {
    val style = Style {
        background(PrimaryGreen)
        shape(RoundedCornerShape(16.dp))
        contentPadding(horizontal = 16.dp, vertical = 8.dp)
    }
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .clickable(
                enabled = true,
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = onClick
            )
            .styleable(null, style),
        contentAlignment = Alignment.Center
    ) {
        if (text != null) {
            Text(
                text,
                fontFamily = SourceSansPro,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                letterSpacing = 1.sp
            )
        }
        if (icon != null) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Surface
            )
        }
    }
}

@Preview
@Composable
fun Preview() {
    PrimaryButton(
//        icon = R.drawable.ic_add_cart,
        text = "Pranish",
        onClick = {}
    )
}
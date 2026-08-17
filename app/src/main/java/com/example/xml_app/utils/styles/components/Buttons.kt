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
import com.example.xml_app.utils.styles.EsewaRed
import com.example.xml_app.utils.styles.PrimaryGreen
import com.example.xml_app.utils.styles.PrimaryGreenTransparent
import com.example.xml_app.utils.styles.Surface
import com.example.xml_app.utils.styles.TextDark300

enum class ButtonVariant {
    PRIMARY, SECONDARY, DESTRUCTIVE, ICON
}

@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    variant: ButtonVariant,
    text: String? = null,
    icon: Int? = null,
    onClick: () -> Unit,
) {
    val style = Style {
        background(
            when (variant) {
                ButtonVariant.PRIMARY -> PrimaryGreen
                ButtonVariant.SECONDARY -> TextDark300
                ButtonVariant.DESTRUCTIVE -> EsewaRed
                ButtonVariant.ICON -> PrimaryGreenTransparent
            }
        )
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
                tint = when (variant) {
                    ButtonVariant.ICON -> PrimaryGreen
                    else -> Surface
                }
            )
        }
    }
}


@Preview
@Composable
fun Preview() {
    AppButton(
        icon = R.drawable.ic_add_cart,
        variant = ButtonVariant.ICON,
        onClick = {}
    )
}
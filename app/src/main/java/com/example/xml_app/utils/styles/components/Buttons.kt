package com.example.xml_app.utils.styles.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.pressed
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.example.xml_app.utils.styles.Light
import com.example.xml_app.utils.styles.OffWhiteBackground
import com.example.xml_app.utils.styles.PrimaryGreen
import com.example.xml_app.utils.styles.PrimaryGreenTransparent
import com.example.xml_app.utils.styles.Surface
import com.example.xml_app.utils.styles.TextDark300

enum class ButtonVariant {
    PRIMARY, SECONDARY, DESTRUCTIVE, OUTLINE, ICON, ROUNDED, GHOST
}

@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    variant: ButtonVariant,
    tint: Color? = null,
    text: String? = null,
    icon: Int? = null,
    onClick: () -> Unit,
    isLoading: Boolean = false
) {

    val style = Style {
        background(
            when (variant) {
                ButtonVariant.PRIMARY, ButtonVariant.ROUNDED -> PrimaryGreen
                ButtonVariant.SECONDARY -> TextDark300
                ButtonVariant.DESTRUCTIVE -> EsewaRed
                ButtonVariant.ICON -> PrimaryGreenTransparent
                ButtonVariant.OUTLINE -> OffWhiteBackground
                ButtonVariant.GHOST -> Color.Transparent
            }
        )
        shape(
            when (variant) {
                ButtonVariant.ROUNDED -> CircleShape
                else -> RoundedCornerShape(16.dp)
            }
        )
        contentPadding(
            horizontal = 16.dp,
            vertical = 8.dp
        )
        border(
            width = when (variant) {
                ButtonVariant.OUTLINE -> 1.dp
                else -> 0.dp
            },
            color = when (variant) {
                ButtonVariant.OUTLINE -> PrimaryGreen
                else -> Color.Transparent
            }
        )
        pressed {
            animate(
                spring(
                    dampingRatio = Spring.DampingRatioHighBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) {
                alpha(0.85f)
                scale(0.96f)
            }
        }


    }

    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource) {
        it.isEnabled = true
    }
    Box(
        modifier = modifier
            .clickable(
                enabled = !isLoading,
                interactionSource = interactionSource,
                indication = null,

                onClick = onClick
            )
            .styleable(styleState, style),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Light,
                trackColor = Surface
            )
            return@Box
        }
        if (text != null) {
            Text(
                text,
                fontFamily = SourceSansPro,
                fontWeight = FontWeight.Medium,
                color = when (variant) {
                    ButtonVariant.OUTLINE -> PrimaryGreen
                    ButtonVariant.GHOST -> TextDark300
                    else -> Color.White
                },
                letterSpacing = 1.sp
            )
        }
        if (icon != null) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = tint ?: when (variant) {
                    ButtonVariant.ICON -> PrimaryGreen
                    ButtonVariant.GHOST -> Color.Unspecified
                    else -> Surface
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun Previews() {
    AppButton(
        variant = ButtonVariant.GHOST,
        onClick = {},
        text="CANCEL",
    )
}
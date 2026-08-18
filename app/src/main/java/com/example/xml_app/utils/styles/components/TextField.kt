package com.example.xml_app.utils.styles.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xml_app.utils.styles.InputBackground
import com.example.xml_app.utils.styles.InputBackgroundCompose
import com.example.xml_app.utils.styles.TextDark100
import com.example.xml_app.utils.styles.TextDark400

@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = InputBackgroundCompose,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        textStyle = TextStyle(
            color = TextDark400,
            fontSize = 16.sp
        ),
        singleLine = true,
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = TextDark100,
                        fontSize = 16.sp,
                        letterSpacing = 0.5.sp
                    )
                }

                innerTextField()
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewTextField() {
    var text by remember { mutableStateOf("") }
    AppTextField(
        value = text,
        onValueChange = { text = it },
        placeholder = "PROMO CODE"
    )
}
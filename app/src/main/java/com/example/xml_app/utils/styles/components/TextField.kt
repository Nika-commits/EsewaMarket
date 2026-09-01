package com.example.xml_app.utils.styles.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xml_app.R
import com.example.xml_app.utils.SourceSansPro
import com.example.xml_app.utils.styles.Error
import com.example.xml_app.utils.styles.InputBackgroundCompose
import com.example.xml_app.utils.styles.TextDark100
import com.example.xml_app.utils.styles.TextDark400

@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean = false,
    errorMessage: String? = null,
    isLoading: Boolean = false,
    startButton: (@Composable () -> Unit)? = null,
    endButton: (@Composable () -> Unit)? = null
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
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
                .then(
                    if (isError) {
                        Modifier.border(
                            2.dp,
                            Error,
                            shape = RoundedCornerShape(16.dp)
                        )
                    } else {
                        Modifier
                    }
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),
            textStyle = TextStyle(
                color = TextDark400,
                fontSize = 16.sp
            ),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {

                    if (startButton != null) {
                        Box(
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            startButton()
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = if (startButton != null) 48.dp else 0.dp,
                                end = if (isLoading) 32.dp else 0.dp
                            )
                    ) {
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

                    // Loading indicator

                    if(endButton != null){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterEnd),
                            contentAlignment = Alignment.CenterEnd
                        ){
                            endButton()
                        }
                    }

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterEnd),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            AppLoadingIndicator()
                        }
                    }
                }
            })
        if (isError && errorMessage != null) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = errorMessage,
                color = Error,
                fontFamily = SourceSansPro,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                letterSpacing = 0.4.sp
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTextField() {
    var text by remember { mutableStateOf("") }
    AppTextField(
        value = text,
        onValueChange = { text = it },
        placeholder = "PROMO CODE",
//        isLoading = true,
        startButton = {
            AppButton(
                variant = ButtonVariant.GHOST,
                onClick = {},
                icon = R.drawable.ic_cancel
            )
        },
        endButton = {
            AppButton(
                variant = ButtonVariant.GHOST,
                onClick = {},
                icon = R.drawable.ic_calender_default
            )
        }
    )
}
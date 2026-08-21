package com.example.xml_app.utils.styles.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.xml_app.utils.styles.PrimaryGreen

@Composable
fun AppLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    strokeWidth: Dp = 4.dp
) {
    CircularProgressIndicator(
        modifier = modifier
            .padding(2.dp)
            .size(size),
        color = PrimaryGreen,
        strokeWidth = strokeWidth
    )
}

@Preview(showBackground = true)
@Composable
fun LoadingPreview() {
    AppLoadingIndicator()
}
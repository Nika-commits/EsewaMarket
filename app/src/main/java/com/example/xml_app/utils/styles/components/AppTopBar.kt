package com.example.xml_app.utils.styles.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xml_app.R
import com.example.xml_app.utils.SourceSansPro
import com.example.xml_app.utils.styles.OffWhiteBackground
import com.example.xml_app.utils.styles.Surface
import com.example.xml_app.utils.styles.TextDark400

@OptIn(ExperimentalFoundationStyleApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onBackClick: () -> Unit = {}
) {
    val style = Style {
        background(OffWhiteBackground)
    }
    val scrollbarBehaviour = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = OffWhiteBackground,
            titleContentColor = TextDark400,
        ),
        title = {
            Text(
                title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = SourceSansPro,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.5.sp
            )
        },
        navigationIcon = {
            IconButton(
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent
                ),
                onClick = onBackClick
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_view_less_small),
                    contentDescription = null
                )
            }
        }

    )
}

@Composable
fun OrderSummaryItem(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Surface)
            .padding(16.dp)
    ) {
        

    }


}

@Preview
@Composable
fun Prev() {
    OrderSummaryItem()
}
package com.example.xml_app.ui.composeUi

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xml_app.R
import com.example.xml_app.utils.SourceSansPro

@Preview(showBackground = true)
@Composable
fun EmptyFavourites(
    modifier: Modifier = Modifier,
    onContinueShopping: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .background(colorResource(R.color.surface))
            .clip(RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Image(
            painter = painterResource(R.drawable.ic_empty_favorite),
            contentDescription = "Empty Favourite",
            modifier = Modifier.size(150.dp)
        )

        Text(
            text = "No Favourites Yet",
            fontSize = 20.sp,
            fontFamily = SourceSansPro,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.15.sp
        )

        Text(
            text = "Add your favourites to wishlist and they will show here",
            fontSize = 16.sp,
            fontFamily = SourceSansPro,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(6.dp),
            color = colorResource(R.color.textDark200)
        )

        Button(
            onClick = onContinueShopping

        ) { }

    }

}
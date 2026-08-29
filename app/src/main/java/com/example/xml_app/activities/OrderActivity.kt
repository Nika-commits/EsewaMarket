package com.example.xml_app.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.xml_app.R
import com.example.xml_app.utils.SourceSansPro
import com.example.xml_app.utils.dto.response.OrderItemResponse
import com.example.xml_app.utils.dto.response.OrderResponse
import com.example.xml_app.utils.styles.OffWhiteBackground
import com.example.xml_app.utils.styles.PrimaryGreen
import com.example.xml_app.utils.styles.Surface
import com.example.xml_app.utils.styles.TextDark200
import com.example.xml_app.utils.styles.TextDark300
import com.example.xml_app.utils.styles.components.AppButton
import com.example.xml_app.utils.styles.components.AppTopBar
import com.example.xml_app.utils.styles.components.ButtonVariant
import com.example.xml_app.viewModel.OrderViewModel

class OrderActivity : AppCompatActivity() {
    enum class OrderFilterType(val label: String) {
        ALL("All"),
        PENDING("Pending"),
        Complete("Complete")
    }

    //changes
    private val viewModel: OrderViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = OffWhiteBackground,
                topBar = {
                    AppTopBar(
                        "My Order",
                        onBackClick = {
                            onBackPressedDispatcher.onBackPressed()
                        }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    val filter by viewModel.filter.collectAsStateWithLifecycle()
                    OrderFilter(
                        selected = filter,
                        onStatusSelect = { newFilter ->
                            viewModel.changeFilter(newFilter)
                        }
                    )
                }
            }

        }
    }
}

@Composable
fun OrderFilter(
    modifier: Modifier = Modifier,
    selected: OrderActivity.OrderFilterType,
    onStatusSelect: (OrderActivity.OrderFilterType) -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OrderActivity.OrderFilterType.entries.forEach { filterType ->
                FilterTab(
                    text = filterType.label,
                    selected = selected == filterType,
                    onClick = {
                        onStatusSelect(filterType)
                    }
                )
            }
        }

        AppButton(
            variant = ButtonVariant.GHOST,
            icon = R.drawable.ic_slider,
            tint = TextDark300,
            onClick = {

            }
        )
    }
}

@Composable
fun FilterTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text,
        fontFamily = SourceSansPro,
        fontSize = if (selected) 16.sp else 14.sp,
        fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
        color = if (selected) PrimaryGreen else TextDark200,
        letterSpacing = if (selected) 0.15.sp else 0.25.sp,
        modifier = modifier
            .clickable(
                enabled = true,
                onClick = {
                    onClick()
                },
                interactionSource = null,
                indication = null
            )
    )
}

@Composable
fun OrderCard(
    order: OrderResponse,
) {
    Card(
        modifier = Modifier
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        )
    ) {


    }

}

@Preview
@Composable
fun OrderFilterPreview() {

    val orderItem1 = OrderItemResponse(
        productId = 1,
        productName = "Addidas Sambas - White",
        productImage = "https://gqtuuqsgkyffgcpbfltk.supabase.co/storage/v1/object/public/product-images/mnml-men's-front-pocket-geo-shorts-mnml-/1770621841231",
        brand = "Core Studio",
        quantity = 2,
        price = 4000
    )
    val orderItem2 = OrderItemResponse(
        productId = 1,
        productName = "Addidas Sambas - White",
        quantity = 1,
        productImage = "https://gqtuuqsgkyffgcpbfltk.supabase.co/storage/v1/object/public/product-images/pranish-nicks/1780819796759",
        brand = "Oxford",
        price = 4000
    )
    val response = OrderResponse(
        id = 1,
        address = "Gothater-8, Kageshori Manohora, Kathmandu, Bagmati",
        phone = "9841890609",
        paymentOption = "Esewa",
        vehicleNumber = "BA 08672",
        deliveryCharge = 200,
        discount = 100,
        status = "Pending",
        totalPrice = 8000,

        orderDate = "2026-08-29T06:16:03.123456Z",
        orderItems = listOf(
            orderItem1,
            orderItem2
        )
    )

    OrderCard(
        response
    )
}
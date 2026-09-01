package com.example.xml_app.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.xml_app.R
import com.example.xml_app.ui.state.OrderUiState
import com.example.xml_app.utils.SourceSansPro
import com.example.xml_app.utils.dto.response.OrderItemResponse
import com.example.xml_app.utils.dto.response.OrderResponse
import com.example.xml_app.utils.formatOrderDate
import com.example.xml_app.utils.styles.OffWhiteBackground
import com.example.xml_app.utils.styles.PrimaryGreen
import com.example.xml_app.utils.styles.Surface
import com.example.xml_app.utils.styles.TextDark200
import com.example.xml_app.utils.styles.TextDark300
import com.example.xml_app.utils.styles.TextDark400
import com.example.xml_app.utils.styles.components.AppButton
import com.example.xml_app.utils.styles.components.AppLoadingIndicator
import com.example.xml_app.utils.styles.components.AppTextField
import com.example.xml_app.utils.styles.components.AppTopBar
import com.example.xml_app.utils.styles.components.ButtonVariant
import com.example.xml_app.viewModel.OrderViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderActivity : AppCompatActivity() {
    enum class OrderFilterType(val label: String) {
        ALL("All"),
        PENDING("Pending"),
        DELIVERED("Delivered")
    }

    private val viewModel: OrderViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getOrders()
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)

                ) {
                    val filter by viewModel.filter.collectAsStateWithLifecycle()
                    OrderFilter(
                        selected = filter,
                        onStatusSelect = { newFilter ->
                            viewModel.changeFilter(newFilter)
                            viewModel.getOrders()
                        }
                    )
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                    when (val state = uiState) {
                        OrderUiState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                AppLoadingIndicator(
                                    size = 80.dp,
                                    strokeWidth = 4.dp
                                )
                            }
                        }

                        OrderUiState.Error -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Failed to load orders")
                            }
                        }

                        is OrderUiState.Success -> {
                            LazyColumn(
                                Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(
                                    items = state.orders,
                                    key = { it.id }
                                ) { order ->
                                    OrderCard(order)
                                }
                            }
                        }
                    }
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
            .padding(vertical = 4.dp, horizontal = 16.dp),
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
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        painter = painterResource(R.drawable.ic_myproducts),
                        contentDescription = null,
                        tint = PrimaryGreen,
                    )
                    Column(
                        modifier = Modifier.padding(vertical = 3.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            "Order No. #${order.id}",
                            fontFamily = SourceSansPro,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            letterSpacing = 0.4.sp
                        )
                        Text(
                            "Placed on ${order.orderDate.formatOrderDate()}",
                            fontFamily = SourceSansPro,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            letterSpacing = 0.4.sp,
                            color = TextDark200
                        )
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "${order.orderItems.size}",
                                fontFamily = SourceSansPro,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = TextDark400
                            )
                            Text(
                                "items purchased",
                                fontFamily = SourceSansPro,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = TextDark200
                            )

                            Text(
                                order.status,
                                fontFamily = SourceSansPro,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = TextDark400
                            )
                        }
                    }
                }

                AppButton(
                    variant = ButtonVariant.GHOST,
                    icon = R.drawable.ic_right_arrow,
                    tint = TextDark400,
                    onClick = {},
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                order.orderItems.forEach { OrderItemsCard(it) }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Total Price",
                    fontFamily = SourceSansPro,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = TextDark300,
                    letterSpacing = 0.25.sp
                )

                Text(
                    "Rs. ${order.totalPrice}",
                    fontFamily = SourceSansPro,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = TextDark400,
                    letterSpacing = 0.15.sp

                )
            }
        }
    }
}

@Composable
fun OrderItemsCard(
    orderItem: OrderItemResponse
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = orderItem.productImage,
            contentDescription = null,
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .padding(16.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                orderItem.productName,
                fontFamily = SourceSansPro,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                letterSpacing = 0.15.sp,
                color = TextDark400
            )
            Text(
                orderItem.brand,
                fontFamily = SourceSansPro,
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp,
                letterSpacing = 1.5.sp,
                color = TextDark200
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    "Rs.",
                    fontFamily = SourceSansPro,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    letterSpacing = 0.25.sp,
                    color = TextDark400
                )

                Text(
                    "${orderItem.price}",
                    fontFamily = SourceSansPro,
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp,
                    letterSpacing = 0.15.sp,
                    color = TextDark400
                )
            }
        }

        Text(
            "X ${orderItem.quantity}",
            modifier = Modifier.align(Alignment.Bottom),
            fontFamily = SourceSansPro,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = TextDark400,
        )
    }

}

@Composable
fun OrderDateFilterSheet(
    onApply: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "Filter by",
            fontFamily = SourceSansPro,
            fontSize = 16.sp,
            letterSpacing = 0.15.sp,
            fontWeight = FontWeight.Medium,
            color = TextDark400
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                Text("From")

                DatePickerFieldToModal(

                )
            }

            Column(
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                Text("To")

                DatePickerFieldToModal()
            }
        }

        AppButton(
            modifier = Modifier.fillMaxWidth(),
            variant = ButtonVariant.PRIMARY,
            text = "APPLY",
            onClick = onApply
        )
    }
}

@Composable
fun DatePickerFieldToModal(
    modifier: Modifier = Modifier
) {
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var showModal by remember { mutableStateOf(false) }

    AppTextField(
        value = selectedDate?.let {
            convertMillisToDate(it)
        } ?: "",
        onValueChange = {},
        placeholder = "Select Date",
        endButton = {
            Icon(
                painter = painterResource(R.drawable.ic_calender_default),
                contentDescription = "Calendar"
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(selectedDate) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showModal = true
                    }
                }
            }
    )

    if (showModal) {
        DatePickerModal(
            onDismiss = { showModal = false },
            onDateSelected = {
                selectedDate = it
            }
        )
    }
}

@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            AppButton(
                variant = ButtonVariant.SECONDARY,
                onClick = onDismiss,
                text = "CANCEL",
            )
        },
        dismissButton = {
            AppButton(
                variant = ButtonVariant.PRIMARY,
                text = "SELECT",
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                }
            )
        }
    ) {
        DatePicker(state = datePickerState)
    }

}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))

}

@Preview(showBackground = true)
@Composable
fun OrderFilterPreview() {
//
//    val orderItem1 = OrderItemResponse(
//        productId = 1,
//        productName = "Addidas Sambas - White",
//        productImage = "https://gqtuuqsgkyffgcpbfltk.supabase.co/storage/v1/object/public/product-images/mnml-men's-front-pocket-geo-shorts-mnml-/1770621841231",
//        brand = "Core Studio",
//        quantity = 2,
//        price = 4000
//    )
//    val orderItem2 = OrderItemResponse(
//        productId = 1,
//        productName = "Addidas Sambas - White",
//        quantity = 1,
//        productImage = "https://gqtuuqsgkyffgcpbfltk.supabase.co/storage/v1/object/public/product-images/pranish-nicks/1780819796759",
//        brand = "Oxford",
//        price = 4000
//    )
//    val response = OrderResponse(
//        id = 1,
//        address = "Gothater-8, Kageshori Manohora, Kathmandu, Bagmati",
//        phone = "9841890609",
//        paymentOption = "Esewa",
//        vehicleNumber = "BA 08672",
//        deliveryCharge = 200,
//        discount = 100,
//        status = "Pending",
//        totalPrice = 8000,
//
//        orderDate = "2026-08-29T06:16:03.123456Z",
//        orderItems = listOf(
//            orderItem1,
//            orderItem2
//        )
//    )

    OrderDateFilterSheet {

    }
}
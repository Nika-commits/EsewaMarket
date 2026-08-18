package com.example.xml_app.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.xml_app.R
import com.example.xml_app.models.Product
import com.example.xml_app.utils.SourceSansPro
import com.example.xml_app.utils.styles.LightCharcoal
import com.example.xml_app.utils.styles.OffWhiteBackground
import com.example.xml_app.utils.styles.PrimaryGreen
import com.example.xml_app.utils.styles.Surface
import com.example.xml_app.utils.styles.TextDark200
import com.example.xml_app.utils.styles.TextDark300
import com.example.xml_app.utils.styles.TextDark400
import com.example.xml_app.utils.styles.components.AppButton
import com.example.xml_app.utils.styles.components.AppTextField
import com.example.xml_app.utils.styles.components.AppTopBar
import com.example.xml_app.utils.styles.components.ButtonVariant
import com.example.xml_app.viewModel.CheckoutViewModel
import kotlinx.coroutines.launch

class CheckoutActivity : AppCompatActivity() {
    private val viewModel: CheckoutViewModel by viewModels()

    @OptIn(ExperimentalFoundationStyleApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initializeUser()
        val activityStyle = Style {
            background(OffWhiteBackground)
        }
        setContent {
            val address by viewModel.address.collectAsStateWithLifecycle()
            val products by viewModel.products.collectAsStateWithLifecycle()
            val productQuantityMap by viewModel.productQuantityMap.collectAsStateWithLifecycle()
            val totalPrice = products.sumOf { product ->
                product.price * (productQuantityMap[product.id] ?: 1)
            }
            val sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )
            var showBottomSheet by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()
            val promoCode by viewModel.promoCode.collectAsStateWithLifecycle()
            val promoCodeResult by viewModel.promoCodeResult.collectAsStateWithLifecycle()
            val isPromoCodeChecking by viewModel.isCheckingPromoCode.collectAsStateWithLifecycle()

            Scaffold(
                modifier = Modifier
                    .styleable(null, activityStyle),
                topBar = {
                    AppTopBar(
                        "Checkout",
                        onBackClick = {
                            onBackPressedDispatcher.onBackPressed()
                        }
                    )
                },
                bottomBar = {
                    BottomBar(totalPrice.toFloat())
                }
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(OffWhiteBackground)
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    item {
                        DeliveryAddress(
                            address = address
                        )
                    }

                    item {
                        Text(
                            "Order Summary  (${products.size})",
                            modifier = Modifier
                                .padding(top = 12.dp, bottom = 8.dp),
                            fontFamily = SourceSansPro,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            letterSpacing = 0.25.sp
                        )
                    }
                    items(products) { product ->
                        val count = productQuantityMap[product.id] ?: return@items
                        OrderSummaryItem(
                            product = product,
                            count = count
                        )
                    }

                    item {
                        AppButton(
                            modifier = Modifier.padding(vertical = 16.dp),
                            variant = ButtonVariant.OUTLINE,
                            text = "HAVE A PROMOCODE?",
                            onClick = {
                                showBottomSheet = true
                            }
                        )
                    }

                    item {
                        Text(
                            "Choose Your Payment Options",
                            modifier = Modifier
                                .padding(top = 12.dp, bottom = 8.dp),
                            fontFamily = SourceSansPro,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            letterSpacing = 0.25.sp
                        )
                    }

                    item {
                        PaymentOptionsList()
                    }
                }

                if (showBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            showBottomSheet = false
                        },
                        sheetState = sheetState,
                        containerColor = Surface,
                        dragHandle = null,
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Text(
                                "Promocode",
                                fontFamily = SourceSansPro,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                letterSpacing = 0.15.sp,
                                color = TextDark400
                            )

                            Column(
                                modifier = Modifier.padding(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {

                                Text(
                                    "Enter Promocode",
                                    fontFamily = SourceSansPro,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    letterSpacing = 0.4.sp,
                                    color = TextDark300
                                )

                                AppTextField(
                                    value = promoCode,
                                    onValueChange = { viewModel.onPromoCodeChange(it) },
                                    placeholder = "Promocode",
                                    isError = promoCodeResult == false,
                                    errorMessage = "Invalid Promo code. Please try again."
                                )

                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                AppButton(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    text = "CANCEL",
                                    variant = ButtonVariant.SECONDARY,
                                    onClick = {
                                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                                            if (!sheetState.isVisible) {
                                                showBottomSheet = false
                                            }
                                        }
                                    },
                                )

                                AppButton(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    text = "APPLY",
                                    variant = ButtonVariant.PRIMARY,
                                    onClick = { viewModel.checkPromoCodeValidity() },
                                    isLoading = isPromoCodeChecking
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryAddress(
    modifier: Modifier = Modifier,
    address: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(OffWhiteBackground)
            .wrapContentHeight()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween

    ) {

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppButton(
                variant = ButtonVariant.ICON,
                icon = R.drawable.ic_address_pin,
                onClick = {}
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Delivery Address",
                    fontFamily = SourceSansPro,
                    fontWeight = FontWeight.Medium,
                    color = LightCharcoal,
                    fontSize = 12.sp,
                    letterSpacing = 0.4.sp
                )

                Text(
                    text = address,
                    fontFamily = SourceSansPro,
                    fontWeight = FontWeight.Medium,
                    color = TextDark400,
                    fontSize = 14.sp
                )
            }
        }
        AppButton(
            variant = ButtonVariant.PRIMARY,
            icon = R.drawable.ic_edit,
            onClick = {}
        )
    }
}

@Composable
fun OrderSummaryItem(
    modifier: Modifier = Modifier,
    product: Product,
    count: Int
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
    ) {
        AsyncImage(
            model = product.imageUrls.firstOrNull(),
            contentDescription = product.name,
            modifier = Modifier
                .padding(16.dp)
                .size(77.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp, horizontal = 8.dp)
        ) {
            Text(
                text = product.name,
                fontSize = 16.sp,
                fontFamily = SourceSansPro,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.15.sp,
                color = TextDark400
            )

            Text(
                text = product.brand,
                fontSize = 10.sp,
                fontFamily = SourceSansPro,
                fontWeight = FontWeight.Normal,
                color = TextDark200,
                letterSpacing = 1.5.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rs.",
                    fontSize = 14.sp,
                    fontFamily = SourceSansPro,
                    fontWeight = FontWeight.Normal,
                    color = PrimaryGreen
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = product.price.toFloat().toString(),
                    fontSize = 20.sp,
                    fontFamily = SourceSansPro,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryGreen
                )
            }
        }

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "x"
            )

            Text(
                text = count.toString()
            )
        }
    }

}

@Composable
fun BottomBar(total: Float) {
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface)
                .heightIn(min = 100.dp)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "Grand Total",
                    color = TextDark400,
                    fontFamily = SourceSansPro,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    letterSpacing = 0.25.sp,
                )
                Text(
                    "*included TAX",
                    color = TextDark200,
                    fontFamily = SourceSansPro,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    letterSpacing = 0.4.sp
                )
            }

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "Rs.",
                    color = PrimaryGreen,
                    fontFamily = SourceSansPro,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    letterSpacing = 0.25.sp
                )
                Text(
                    text = total.toString(),
                    color = PrimaryGreen,
                    fontFamily = SourceSansPro,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                    letterSpacing = 0.15.sp
                )
            }
        }
        AppButton(
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-24).dp),
            variant = ButtonVariant.ROUNDED,
            icon = R.drawable.ic_top_arrow,
            onClick = {}
        )
    }

}

@Composable
fun PaymentOptionsList() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = true,
                    onClick = {

                    }
                )
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically

            ) {
                AppButton(
                    variant = ButtonVariant.GHOST,
                    icon = R.drawable.ic_dailybuybonus,
                    onClick = {}
                )
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "Cash on Delivery",
                    fontFamily = SourceSansPro,
                    color = TextDark300,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    letterSpacing = 0.1.sp
                )
            }
            AppButton(
                variant = ButtonVariant.GHOST,
                icon = R.drawable.ic_right_arrow,
                onClick = {}
            )

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = true,
                    onClick = {}
                )
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically

            ) {
                AppButton(
                    variant = ButtonVariant.GHOST,
                    icon = R.drawable.ic_esewa_grey,
                    tint = Color.Unspecified,
                    onClick = {}
                )
                Text(
                    "Pay with eSewa",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontFamily = SourceSansPro,
                    color = TextDark300,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    letterSpacing = 0.1.sp
                )
            }
            AppButton(
                variant = ButtonVariant.GHOST,
                icon = R.drawable.ic_right_arrow,
                tint = TextDark300,
                onClick = {}
            )

        }
    }
}


@Preview(showBackground = true)
@Composable
fun Prev() {
    PaymentOptionsList()
}

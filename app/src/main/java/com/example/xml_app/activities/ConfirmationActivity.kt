package com.example.xml_app.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.xml_app.BuildConfig
import com.example.xml_app.R
import com.example.xml_app.ui.state.ConfirmationOrderUiState
import com.example.xml_app.ui.state.ConfirmationUiState
import com.example.xml_app.utils.SourceSansPro
import com.example.xml_app.utils.dto.request.PaymentOptions
import com.example.xml_app.utils.dto.response.OrderItemResponse
import com.example.xml_app.utils.dto.response.OrderResponse
import com.example.xml_app.utils.styles.EsewaRed
import com.example.xml_app.utils.styles.OffWhiteBackground
import com.example.xml_app.utils.styles.Surface
import com.example.xml_app.utils.styles.TextDark200
import com.example.xml_app.utils.styles.TextDark300
import com.example.xml_app.utils.styles.TextDark400
import com.example.xml_app.utils.styles.TextLight
import com.example.xml_app.utils.styles.components.AppButton
import com.example.xml_app.utils.styles.components.AppLoadingIndicator
import com.example.xml_app.utils.styles.components.AppTopBar
import com.example.xml_app.utils.styles.components.ButtonVariant
import com.example.xml_app.viewModel.ConfirmationViewModel
import com.f1soft.esewapaymentsdk.EsewaConfiguration
import com.f1soft.esewapaymentsdk.EsewaPayment
import com.f1soft.esewapaymentsdk.ui.screens.EsewaPaymentActivity

class ConfirmationActivity : AppCompatActivity() {
    companion object {
        const val ID = "ORDER_ID"
        fun startActivity(
            context: Context,
            orderId: Int
        ) {
            val intent = Intent(context, ConfirmationActivity::class.java).apply {
                putExtra(ID, orderId)
            }
            context.startActivity(intent)
        }

    }

    private val viewModel: ConfirmationViewModel by viewModels()

    private val eSewaPaymentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            RESULT_OK -> {
                val message = result.data?.getStringExtra(EsewaPayment.EXTRA_RESULT_MESSAGE)
                Log.i("Esewa", "$message")
            }

            RESULT_CANCELED -> {
                Log.i("Esewa", "Payment Cancelled")
            }

            EsewaPayment.RESULT_EXTRAS_INVALID -> {
                val message = result.data?.getStringExtra(EsewaPayment.EXTRA_RESULT_MESSAGE)
                Log.e("Esewa", "$message")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val orderId = intent.getIntExtra(ID, -1)
        if (orderId == -1) finish()

        viewModel.getOrder(orderId)

        setContent {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize(),
                topBar = {
                    AppTopBar(
                        title = "Confirmation",
                        onBackClick = {
                            onBackPressedDispatcher.onBackPressed()
                        }
                    )
                },
                containerColor = OffWhiteBackground
            ) { innerPadding ->
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                when (val state = uiState) {
                    ConfirmationUiState.Loading ->
                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            AppLoadingIndicator(
                                size = 100.dp,
                                strokeWidth = 8.dp
                            )
                        }

                    is ConfirmationUiState.Success -> {
                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OrderResponseCard(
                                state.order
                            )

                            AppButton(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                variant = ButtonVariant.PRIMARY,
                                onClick = {
                                    when (state.order.paymentOption) {
                                        PaymentOptions.Esewa.toString() -> {
                                            val eSewaPayment = EsewaPayment(
                                                amount = state.order.totalPrice.toString(),
                                                productName = "Product1",
                                                productUniqueId = "1",
                                            )
                                            initiateEsewaPayment(eSewaPayment = eSewaPayment)
                                        }

                                        PaymentOptions.Cash_On_Delivery.toString() -> {
                                            viewModel.updateOrderStatusToPending()
                                        }
                                    }

                                },
                                text = "CONFIRM"
                            )
                        }
                    }

                    ConfirmationUiState.Error -> {
                        finish()
                    }
                }

                val orderState by viewModel.confirmationOrderUiState.collectAsStateWithLifecycle()

                when (orderState) {
                    ConfirmationOrderUiState.Idle -> Unit

                    ConfirmationOrderUiState.Loading,
                    ConfirmationOrderUiState.Error -> {
                        PlacingOrderDialog(
                            onDismissRequest = {},
                            onRetry = {},
                            state = orderState
                        )
                    }

                    ConfirmationOrderUiState.Success -> {

                    }

                }
            }
        }
    }

    fun initiateEsewaPayment(
        eSewaPayment: EsewaPayment
    ) {
        Log.d("Esewa", BuildConfig.EsewaClientId)
        Log.d("Esewa", BuildConfig.EsewaClientSecret)
        Log.d("Esewa", EsewaConfiguration.ENVIRONMENT_TEST)
        val eSewaConfiguration = EsewaConfiguration(
            clientId = BuildConfig.EsewaClientId,
            secretKey = BuildConfig.EsewaClientSecret,
            environment = EsewaConfiguration.ENVIRONMENT_TEST
        )


        val intent = Intent(this, EsewaPaymentActivity::class.java).apply {
            putExtra(EsewaConfiguration.ESEWA_CONFIGURATION, eSewaConfiguration)
            putExtra(EsewaPayment.ESEWA_PAYMENT, eSewaPayment)
        }
        eSewaPaymentLauncher.launch(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }
}

@Composable
fun PlacingOrderDialog(
    onDismissRequest: () -> Unit,
    onRetry: () -> Unit,
    state: ConfirmationOrderUiState
) {
    Dialog(
        onDismissRequest = {
            onDismissRequest()
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )

    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Surface
            )

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                when (state) {
                    ConfirmationOrderUiState.Loading -> {
                        AppLoadingIndicator(
                            size = 60.dp,
                            strokeWidth = 4.dp
                        )

                        Text(
                            "Placing your Order...",
                            fontFamily = SourceSansPro,
                            fontWeight = FontWeight.Medium,
                            color = TextDark300,
                            letterSpacing = 0.5.sp
                        )
                    }

                    ConfirmationOrderUiState.Error -> {
                        Icon(
                            painter = painterResource(R.drawable.ic_error),
                            contentDescription = "Order failed",
                            tint = EsewaRed,
                            modifier = Modifier.size(52.dp)
                        )

                        Text(
                            text = "Something went wrong while placing your order. Please try again.",
                            fontFamily = SourceSansPro,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = TextDark200,
                            textAlign = TextAlign.Center
                        )

                        AppButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = "TRY AGAIN",
                            variant = ButtonVariant.SECONDARY,
                            onClick = onRetry
                        )
                    }

                    else -> {}
                }
            }
        }
    }
}

@Composable
fun OrderResponseCard(
    order: OrderResponse
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),

        ) {

        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Payment Details",
                fontFamily = SourceSansPro,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                letterSpacing = 0.1.sp
            )

            order.orderItems.forEach { item ->
                ProductItem(item)
            }

            InfoRow(
                "Delivery Address",
                order.address.substringBefore(',')
            )

            InfoRow(
                "Payment Option",
                order.paymentOption
            )

            InfoRow(
                "Vehicle Number",
                order.vehicleNumber
            )

            InfoRow(
                "Discount",
                order.discount.toString()
            )

            InfoRow(
                "Delivery Charge",
                order.deliveryCharge.toString()
            )

            HorizontalDivider(
                thickness = 1.dp,
                color = TextLight
            )
            InfoRow(
                "Total Paying Amount",
                order.totalPrice.toString()
            )
        }
    }

}

@Composable
fun ProductItem(
    orderItemResponse: OrderItemResponse
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Product Item (${orderItemResponse.quantity})",
            color = TextDark300,
            fontFamily = SourceSansPro,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            letterSpacing = 0.4.sp
        )

        InfoRow(
            "Name",
            orderItemResponse.productName
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = TextLight
        )
    }

}

@Composable
fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontFamily = SourceSansPro,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = TextDark200,
            letterSpacing = 0.4.sp
        )

        Text(
            value,
            fontFamily = SourceSansPro,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = TextDark400,
            letterSpacing = 0.1.sp
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OrderResponseCardPreview() {
    PlacingOrderDialog(
        onDismissRequest = {},
        onRetry = {},
        state = ConfirmationOrderUiState.Loading
    )
}

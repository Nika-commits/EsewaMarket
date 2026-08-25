package com.example.xml_app.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.xml_app.utils.dto.response.OrderItemResponse
import com.example.xml_app.utils.dto.response.OrderResponse
import com.example.xml_app.utils.styles.Surface
import com.example.xml_app.utils.styles.components.AppLoadingIndicator
import com.example.xml_app.utils.styles.components.AppTopBar
import com.example.xml_app.viewModel.ConfirmationViewModel

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val orderId = intent.getIntExtra(ID, -1)
        if (orderId == -1) finish()

        setContent {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    AppTopBar(
                        title = "Confirmation",
                        onBackClick = {
                            onBackPressedDispatcher.onBackPressed()
                        }
                    )
                }
            ) { innerPadding ->
                val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
                if (isLoading) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AppLoadingIndicator(
                            size = 100.dp,
                            strokeWidth = 8.dp
                        )
                    }
                } else {
                    val order by viewModel.orderResponse.collectAsStateWithLifecycle()
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp)
                            .background(Surface)
                            .padding(16.dp),

                        ) {
                        Text("Confirmation Activity")
                    }
                }
            }
        }
    }
}

@Composable
fun OrderResponseCard(
    order: OrderResponse
) {


}

@Preview
@Composable
fun OrderResponseCardPreview() {
    val orderItem1 = OrderItemResponse(
        productId = 1,
        productName = "Addidas Sambas - White",
        quantity = 2,
        price = 4000
    )
    val orderItem2 = OrderItemResponse(
        productId = 1,
        productName = "Addidas Sambas - White",
        quantity = 2,
        price = 4000
    )
    val response = OrderResponse(
        id = 1,
        address = "Gothater-8, Kageshori Manohora, Kathmandu, Bagmati",
        phone = "9841890609",
        paymentOptions = "Esewa",
        vehicleNumber = "BA 08672",
        deliveryCharge = 200,
        discount = 100,
        status = "Pending",
        totalPrice = 8000,
        orderItems = listOf(
            orderItem1,
            orderItem2
        )
    )

    OrderResponseCard(response)
}

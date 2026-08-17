package com.example.xml_app.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.xml_app.utils.styles.LightCharcoal
import com.example.xml_app.utils.styles.OffWhiteBackground
import com.example.xml_app.utils.styles.TextDark400
import com.example.xml_app.utils.styles.components.AppButton
import com.example.xml_app.utils.styles.components.AppTopBar
import com.example.xml_app.utils.styles.components.ButtonVariant
import com.example.xml_app.viewModel.CheckoutViewModel

class CheckoutActivity : AppCompatActivity() {
    private val viewModel: CheckoutViewModel by viewModels()

    @OptIn(ExperimentalFoundationStyleApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityStyle = Style {
            background(OffWhiteBackground)
        }
        setContent {
            val address by viewModel.address.collectAsStateWithLifecycle()
            Scaffold(
                modifier = Modifier
                    .styleable(null, activityStyle),
                topBar = {
                    AppTopBar(
                        "Checkout"
                    )
                }
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .background(OffWhiteBackground)
                        .padding(innerPadding)
                        .padding(vertical = 16.dp)
                ) {
                    item {
                    DeliveryAddress(
                        address = address
                    )
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

@Preview(showBackground = true)
@Composable
fun Prev() {
    val address = "Pulchowk, Lalitpur-20"

    DeliveryAddress(
        address = address
    )
}
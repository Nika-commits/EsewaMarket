package com.example.xml_app.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xml_app.R
import com.example.xml_app.utils.SourceSansPro
import com.example.xml_app.utils.dto.request.AddressLabel
import com.example.xml_app.utils.dto.response.UserAddressResponse
import com.example.xml_app.utils.styles.OffWhiteBackground
import com.example.xml_app.utils.styles.PrimaryGreen
import com.example.xml_app.utils.styles.PrimaryGreenTransparent
import com.example.xml_app.utils.styles.Surface
import com.example.xml_app.utils.styles.TextDark400
import com.example.xml_app.utils.styles.components.AppButton
import com.example.xml_app.utils.styles.components.AppTopBar
import com.example.xml_app.utils.styles.components.ButtonVariant
import com.example.xml_app.viewModel.ShippingAddressViewModel

class ShippingAddressActivity : AppCompatActivity() {
    private val viewModel: ShippingAddressViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(
                topBar = {
                    AppTopBar(
                        title = "Shipping Address",
                        onBackClick = {
                            onBackPressedDispatcher.onBackPressed()
                        }
                    )
                }
            ) { innerPadding ->
                ShippingAddressScreen(
                    modifier = Modifier.padding(innerPadding),
                    onAddAddress = {}
                )
            }
        }
    }
}

@Composable
fun ShippingAddressScreen(
    modifier: Modifier = Modifier,
    onAddAddress: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

        }
        AppButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            variant = ButtonVariant.PRIMARY,
            text = "ADD ADDRESS",
            onClick = onAddAddress
        )
    }
}


@Composable
fun AddressCard(
    address: UserAddressResponse
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        border = if (address.isDefaultAddress) BorderStroke(width = 1.dp, color = PrimaryGreen) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            AppButton(
                variant = ButtonVariant.ICON,
                onClick = {},
                icon = R.drawable.ic_address_pin
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        address.fullName,
                        fontFamily = SourceSansPro,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 0.4.sp,
                        color = TextDark400,
                        fontSize = 12.sp
                    )

                    AddressLabelChip(address.label)
                }

                Text(
                    address.fullAddress,
                    maxLines = 2,
                    fontFamily = SourceSansPro,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.1.sp,
                    color = TextDark400,
                    fontSize = 14.sp
                )
            }

            AppButton(
                variant = ButtonVariant.GHOST,
                onClick = {},
                icon = R.drawable.ic_overflow_option
            )
        }
    }
}

@Composable
fun AddressLabelChip(
    label: AddressLabel
) {
    Text(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (label == AddressLabel.Home) PrimaryGreen else PrimaryGreenTransparent)
            .padding(vertical = 2.dp, horizontal = 8.dp),
        text = label.name,
        fontFamily = SourceSansPro,
        color = if (label == AddressLabel.Home) Surface else PrimaryGreen,
        fontSize = 14.sp,
    )
}


@Preview(showBackground = true)
@Composable
fun ShippingAddressPreview() {

    val response = UserAddressResponse(
        id = 1,
        userId = 1,
        fullName = "Pranish Chaulagain",
        phoneNumber = "9748285043",
        fullAddress = "Tej Binayak Chowk, Gothatar-8, Kageshwori Manohora",
        label = AddressLabel.Home,
        isDefaultAddress = false,
        createdAt = "2026-08-29",
        updatedAt = "2026-09-22",
        isDefaultShippingAddress = false
    )
//
//    AddressCard(
//        response
//    )
}



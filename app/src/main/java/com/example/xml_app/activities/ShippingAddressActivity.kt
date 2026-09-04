package com.example.xml_app.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.xml_app.R
import com.example.xml_app.ui.state.ShippingAddressUiEvent
import com.example.xml_app.ui.state.ShippingAddressUiState
import com.example.xml_app.utils.CustomComposeSnackBar
import com.example.xml_app.utils.SourceSansPro
import com.example.xml_app.utils.custom.SwipableItemsWithActions
import com.example.xml_app.utils.dto.request.AddressLabel
import com.example.xml_app.utils.dto.response.UserAddressResponse
import com.example.xml_app.utils.styles.EsewaRed
import com.example.xml_app.utils.styles.OffWhiteBackground
import com.example.xml_app.utils.styles.PrimaryGreen
import com.example.xml_app.utils.styles.PrimaryGreenTransparent
import com.example.xml_app.utils.styles.Surface
import com.example.xml_app.utils.styles.TextDark200
import com.example.xml_app.utils.styles.TextDark300
import com.example.xml_app.utils.styles.TextDark400
import com.example.xml_app.utils.styles.components.AppButton
import com.example.xml_app.utils.styles.components.AppLoadingIndicator
import com.example.xml_app.utils.styles.components.AppTopBar
import com.example.xml_app.utils.styles.components.ButtonVariant
import com.example.xml_app.viewModel.ShippingAddressViewModel

class ShippingAddressActivity : AppCompatActivity() {
    private val viewModel: ShippingAddressViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            Scaffold(
                topBar = {
                    AppTopBar(
                        title = "Shipping Address",
                        onBackClick = {
                            onBackPressedDispatcher.onBackPressed()
                        }
                    )
                },
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState
                    ) { snackbarData ->
                        CustomComposeSnackBar(
                            snackBarData = snackbarData
                        )
                    }
                }
            ) { innerPadding ->
                val state by viewModel.state.collectAsStateWithLifecycle()
                val isDeleting by viewModel.isDeleting.collectAsStateWithLifecycle()
                val openDeleteDialog = remember { mutableStateOf<Int?>(null) }

                LaunchedEffect(Unit) {
                    viewModel.events.collect { event ->
                        when (event) {
                            ShippingAddressUiEvent.DeleteSuccess -> {
                                openDeleteDialog.value = null
                                snackbarHostState.showSnackbar(
                                    "Address Deleted Successfully",
                                    actionLabel = "OK",
                                    duration = SnackbarDuration.Short
                                )
                            }

                            is ShippingAddressUiEvent.Error -> {
                                openDeleteDialog.value = null
                                snackbarHostState.showSnackbar(
                                    "Failed to delete address",
                                    actionLabel = "OK",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    }
                }

                when (val addresses = state) {
                    ShippingAddressUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .padding(innerPadding)
                                .background(OffWhiteBackground)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            AppLoadingIndicator(
                                strokeWidth = 4.dp,
                                size = 80.dp
                            )
                        }
                    }

                    ShippingAddressUiState.Error -> {
                        Box(
                            modifier = Modifier
                                .padding(innerPadding)
                                .background(OffWhiteBackground)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("An Error Occurred")
                        }
                    }


                    ShippingAddressUiState.Empty -> {
                        Box(
                            modifier = Modifier
                                .padding(innerPadding)
                                .background(OffWhiteBackground)
                                .fillMaxSize(),
                            contentAlignment = Alignment.TopStart
                        ) {
                            EmptyAddressCard(
                                onAddNewAddress = {
                                    AddNewAddressActivity.startActivity(
                                        this@ShippingAddressActivity,
                                        AddNewAddressActivity.Companion.MODE.ADD,
                                        null
                                    )
                                }
                            )
                        }

                    }

                    is ShippingAddressUiState.Success -> {
                        ShippingAddressScreen(
                            modifier = Modifier.padding(innerPadding),
                            addresses = addresses.data,
                            onAddAddress = {
                                AddNewAddressActivity.startActivity(
                                    this,
                                    AddNewAddressActivity.Companion.MODE.ADD,
                                    null
                                )
                            },
                            onEditAddress = {
                                AddNewAddressActivity.startActivity(
                                    this,
                                    AddNewAddressActivity.Companion.MODE.EDIT,
                                    it
                                )
                            },
                            onDeleteAddress = {
                                openDeleteDialog.value = it
                            }
                        )
                    }
                }

                if (openDeleteDialog.value != null) {
                    AlertDialog(
                        containerColor = Surface,
                        title = {
                            Text(
                                "Do you want to delete this",
                                fontSize = 14.sp,
                                fontFamily = SourceSansPro,
                                fontWeight = FontWeight.Medium,
                                color = TextDark400
                            )
                        },
                        text = {
                            Text(
                                "This action cannot be undone.",
                                fontSize = 12.sp,
                                fontFamily = SourceSansPro,
                                fontWeight = FontWeight.Normal,
                                color = TextDark300
                            )
                        },
                        onDismissRequest = {
                            openDeleteDialog.value = null
                        },
                        confirmButton = {
                            AppButton(
                                variant = ButtonVariant.PRIMARY,
                                text = "DELETE",
                                onClick = {
                                    if (openDeleteDialog.value != null) {
                                        viewModel.deleteAddress(openDeleteDialog.value!!)
                                    }
                                },
                                isLoading = isDeleting
                            )
                        },
                        dismissButton = {
                            AppButton(
                                variant = ButtonVariant.SECONDARY,
                                text = "CANCEL",
                                onClick = {
                                    openDeleteDialog.value = null
                                }
                            )
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAddresses()
    }
}

@Composable
fun ShippingAddressScreen(
    modifier: Modifier = Modifier,
    addresses: List<UserAddressResponse>,
    onAddAddress: () -> Unit,
    onEditAddress: (id: Int) -> Unit,
    onDeleteAddress: (id: Int) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = addresses,
                key = { it.id }
            ) { address ->
                SwipableItemsWithActions(
                    content = {
                        AddressCard(address)
                    },
                    actions = {
                        Icon(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100))
                                .background(PrimaryGreen)
                                .padding(12.dp)
                                .clickable(
                                    enabled = true,
                                    onClick = {
                                        onEditAddress(address.id)
                                    },
                                ),
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = null,
                            tint = Surface
                        )
                        Icon(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100))
                                .background(EsewaRed)
                                .padding(12.dp)
                                .clickable(
                                    enabled = true,
                                    onClick = {
                                        onDeleteAddress(address.id)
                                    }
                                ),
                            painter = painterResource(R.drawable.ic_trash),
                            contentDescription = null,
                            tint = Surface
                        )
                    },
                    onExpanded = {},
                    onCollapsed = {},
                    isRevealed = false

                )
            }
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
    EmptyAddressCard(
        onAddNewAddress = {}
    )

}

@Composable
fun EmptyAddressCard(
    onAddNewAddress: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                modifier = Modifier.size(200.dp),
                painter = painterResource(R.drawable.img_empty_address_ship),
                contentDescription = null
            )

            Text(
                "No address added yet !",
                fontFamily = SourceSansPro,
                fontSize = 20.sp,
                letterSpacing = 0.15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark400
            )

            Text(
                "You have not added any shipping address yet.",
                fontFamily = SourceSansPro,
                fontSize = 16.sp,
                letterSpacing = 0.5.sp,
                color = TextDark200,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            AppButton(
                variant = ButtonVariant.PRIMARY,
                onClick = onAddNewAddress,
                text = "ADD ADDRESS NOW"
            )
        }
    }
}
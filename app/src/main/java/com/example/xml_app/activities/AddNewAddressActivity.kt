package com.example.xml_app.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.xml_app.R
import com.example.xml_app.ui.state.AddShippingAddressUiState
import com.example.xml_app.utils.CustomComposeSnackBar
import com.example.xml_app.utils.SourceSansPro
import com.example.xml_app.utils.dto.request.AddressLabel
import com.example.xml_app.utils.dto.request.CreateAddressRequest
import com.example.xml_app.utils.styles.Black
import com.example.xml_app.utils.styles.OffWhiteBackground
import com.example.xml_app.utils.styles.PrimaryGreen
import com.example.xml_app.utils.styles.SecondaryGreen
import com.example.xml_app.utils.styles.Surface
import com.example.xml_app.utils.styles.TextDark100
import com.example.xml_app.utils.styles.TextDark300
import com.example.xml_app.utils.styles.TextDark400
import com.example.xml_app.utils.styles.components.AppButton
import com.example.xml_app.utils.styles.components.AppLoadingIndicator
import com.example.xml_app.utils.styles.components.AppTextField
import com.example.xml_app.utils.styles.components.AppTopBar
import com.example.xml_app.utils.styles.components.ButtonVariant
import com.example.xml_app.viewModel.AddNewAddressViewModel

class AddNewAddressActivity : AppCompatActivity() {
    companion object {
        const val TYPE = "type"
        const val ADDRESS_ID = "address_id"

        enum class MODE {
            ADD, EDIT
        }

        fun startActivity(
            context: Context,
            mode: MODE,
            addressId: Int?
        ) {
            val intent = Intent(context, AddNewAddressActivity::class.java).apply {
                putExtra(TYPE, mode.name)
                putExtra(ADDRESS_ID, addressId)
            }
            context.startActivity(intent)
        }
    }

    private val mapsActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == RESULT_OK){
            val address = result.data?.getStringExtra(MapsActivity.EXTRA_ADDRESS)
            if(address != null){
                viewModel.onEvent(
                    AddressFormEvent.FullAddressChanged(address)
                )
            }
        }
    }

    sealed interface AddressFormEvent {
        data class FullNameChanged(val value: String) : AddressFormEvent
        data class PhoneNumberChanged(val value: String) : AddressFormEvent
        data class FullAddressChanged(val value: String) : AddressFormEvent
        data class LabelChanged(val value: AddressLabel) : AddressFormEvent
        data class DefaultAddressChanged(val value: Boolean) : AddressFormEvent
        data class DefaultShippingAddressChanged(val value: Boolean) : AddressFormEvent
    }

    private val viewModel: AddNewAddressViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mode = when (intent.getStringExtra(TYPE)) {
            "ADD" -> MODE.ADD
            "EDIT" -> MODE.EDIT
            else -> MODE.ADD

        }
        val addressId = intent.getIntExtra(ADDRESS_ID, -1)
        if (addressId != -1 && mode == MODE.EDIT) {
            viewModel.getCurrentAddress(addressId)
        }

        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            LaunchedEffect(Unit) {
                viewModel.snackbarMessage.collect { message ->
                    snackbarHostState.showSnackbar(message)
                }
            }
            Scaffold(
                topBar = {
                    AppTopBar(
                        title = when (mode) {
                            MODE.ADD -> "Add new Address"
                            MODE.EDIT -> "Edit your address"
                        },
                        onBackClick = {
                            onBackPressedDispatcher.onBackPressed()
                        }
                    )
                },
                containerColor = OffWhiteBackground,
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
                val formData by viewModel.formData.collectAsStateWithLifecycle()
                val state by viewModel.state.collectAsStateWithLifecycle()
                when (state) {
                    AddShippingAddressUiState.Error -> {
                        Text(
                            modifier = Modifier.padding(innerPadding),
                            text = "An Error Occurred"
                        )
                    }

                    AddShippingAddressUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            AppLoadingIndicator(
                                strokeWidth = 4.dp,
                                size = 80.dp
                            )
                        }
                    }

                    AddShippingAddressUiState.Success -> {
                        DetailsFormScreen(
                            modifier = Modifier.padding(innerPadding),
                            address = formData,
                            onSave = {},
                            onEvent = {
                                viewModel.onEvent(it)
                            }
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun DetailsFormScreen(
    address: CreateAddressRequest,
    modifier: Modifier = Modifier,
    onEvent: (AddNewAddressActivity.AddressFormEvent) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Surface)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                "Details for shipping",
                fontFamily = SourceSansPro,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                letterSpacing = 0.15.sp,
                color = TextDark400
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    "Full Name",
                    fontFamily = SourceSansPro,
                    color = TextDark300,
                    fontSize = 12.sp,
                    letterSpacing = 0.4.sp
                )
                AppTextField(
                    value = address.fullName,
                    onValueChange = {
                        onEvent(AddNewAddressActivity.AddressFormEvent.FullNameChanged(it))
                    },
                    placeholder = "Enter Full Name"
                )

            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    "Mobile Number",
                    fontFamily = SourceSansPro,
                    color = TextDark300,
                    fontSize = 12.sp,
                    letterSpacing = 0.4.sp
                )
                AppTextField(
                    value = address.phoneNumber,
                    onValueChange = {
                        onEvent(AddNewAddressActivity.AddressFormEvent.PhoneNumberChanged(it))
                    },
                    placeholder = "Enter mobile no."
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    "Address",
                    fontFamily = SourceSansPro,
                    color = TextDark300,
                    fontSize = 12.sp,
                    letterSpacing = 0.4.sp
                )
                AppTextField(
                    value = address.fullAddress,
                    onValueChange = {
                        onEvent(AddNewAddressActivity.AddressFormEvent.FullAddressChanged(it))
                    },
                    placeholder = "Enter Address",
                    endButton = {
                        Icon(
                            modifier = Modifier.clickable(
                                enabled = true,
                                onClick = {

                                }
                            ),
                            painter = painterResource(R.drawable.ic_address_pin),
                            contentDescription = null,
                        )
                    }
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    "Select a label",
                    fontFamily = SourceSansPro,
                    color = TextDark300,
                    fontSize = 12.sp,
                    letterSpacing = 0.4.sp
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AddressLabel.entries.forEach { label ->
                        AppButton(
                            variant = if (address.label == label) {
                                ButtonVariant.PRIMARY
                            } else {
                                ButtonVariant.OUTLINE
                            },
                            onClick = {
                                onEvent(
                                    AddNewAddressActivity.AddressFormEvent.LabelChanged(label)
                                )
                            },
                            text = label.name
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(),
                thickness = 0.5.dp,
                color = TextDark100
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Make this as a default shipping address",
                    fontFamily = SourceSansPro,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = TextDark300,
                    modifier = Modifier.weight(1f)
                )

                Switch(
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = PrimaryGreen,
                        checkedTrackColor = SecondaryGreen,
                        uncheckedThumbColor = Black,
                    ),

                    checked = address.isDefaultAddress,
                    onCheckedChange = {
                        onEvent(
                            AddNewAddressActivity.AddressFormEvent.DefaultAddressChanged(it)
                        )
                    }
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Make this as a default billing Address",
                    fontFamily = SourceSansPro,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = TextDark300,
                    modifier = Modifier.weight(1f)
                )

                Switch(
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = PrimaryGreen,
                        checkedTrackColor = SecondaryGreen,
                        uncheckedThumbColor = Black,
                    ),
                    modifier = Modifier,
                    checked = address.isDefaultShippingAddress,
                    onCheckedChange = {
                        onEvent(
                            AddNewAddressActivity.AddressFormEvent.DefaultShippingAddressChanged(it)
                        )
                    }
                )
            }
        }

        AppButton(
            modifier = Modifier.fillMaxWidth(),
            variant = ButtonVariant.PRIMARY,
            onClick = onSave,
            text = "SAVE"
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FormPreview() {
    DetailsFormScreen(
        address = CreateAddressRequest(
            fullName = "Pranish Chaulagain",
            phoneNumber = "9841890609",
            fullAddress = "Gothatar-8, Kathmandu",
            label = AddressLabel.Home,
            isDefaultAddress = true,
            isDefaultShippingAddress = true
        ),
        modifier = Modifier.fillMaxWidth(),
        onSave = {

        },
        onEvent = {

        }
    )
}
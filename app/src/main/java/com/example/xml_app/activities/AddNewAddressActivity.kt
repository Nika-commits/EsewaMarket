package com.example.xml_app.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.xml_app.ui.state.AddShippingAddressUiState
import com.example.xml_app.utils.dto.request.AddressLabel
import com.example.xml_app.utils.dto.request.CreateAddressRequest
import com.example.xml_app.utils.styles.Surface
import com.example.xml_app.utils.styles.components.AppButton
import com.example.xml_app.utils.styles.components.AppLoadingIndicator
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
    onSave: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Surface),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

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
            fullName = "",
            phoneNumber = "",
            fullAddress = "",
            label = AddressLabel.Home,
            isDefaultAddress = false,
            isDefaultShippingAddress = false
        ),
        modifier = Modifier.fillMaxWidth(),
        onSave = {}
    )
}
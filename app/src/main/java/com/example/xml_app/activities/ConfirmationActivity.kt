package com.example.xml_app.activities

import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.xml_app.utils.styles.Surface
import com.example.xml_app.utils.styles.components.AppTopBar

class ConfirmationActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
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
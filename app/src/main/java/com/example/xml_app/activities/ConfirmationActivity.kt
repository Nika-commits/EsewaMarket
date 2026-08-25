package com.example.xml_app.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
package com.example.xml_app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.xml_app.models.ProductSerializer
import com.example.xml_app.models.ProductStates

val Context.productDataStore: DataStore<ProductStates> by dataStore(
    fileName = "product_state.json",
    serializer = ProductSerializer
)


package com.example.xml_app.data.dao

import androidx.room3.Upsert
import com.example.xml_app.entities.Cart

interface CartDao {

    @Upsert
    suspend fun upsert(cart: Cart)

}
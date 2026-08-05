package com.example.xml_app.data.dao

import androidx.room3.Delete
import androidx.room3.Upsert
import com.example.xml_app.entities.CartItem

interface CartItemDao {
    @Upsert
    suspend fun upsert(cartItem: CartItem)

    @Delete
    suspend fun delete(cartItem: CartItem)
}
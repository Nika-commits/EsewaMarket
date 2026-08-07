package com.example.xml_app.repository

import com.example.xml_app.data.dao.CartDao
import com.example.xml_app.entities.Cart
import com.example.xml_app.entities.CartItem
import kotlinx.coroutines.flow.Flow

class CartRepository(
    private val cartDao: CartDao
) {
    suspend fun getOrCreateCart(userId: Int): Cart {
        val existingCart = cartDao.getCartByUserId(userId)
        if (existingCart != null) return existingCart

        val id = cartDao.insert(
            Cart(userId = userId)
        )

        return Cart(
            uid = id.toInt(),
            userId = userId
        )
    }

    suspend fun increment(userId: Int, productId: Int) {
        val cart = getOrCreateCart(userId)
        val existing = cartDao.getCartItem(cartId = cart.uid, productId = productId)

        if (existing == null) {
            cartDao.insertCartItem(
                CartItem(
                    cartId = cart.uid,
                    productId = productId,
                    quantity = 1
                )
            )
        } else {
            cartDao.updateCartItem(
                existing.copy(
                    quantity = existing.quantity + 1
                )
            )
        }
    }

    suspend fun decrement(userId: Int, productId: Int) {
        val cart = getOrCreateCart(userId)
        val existing = cartDao.getCartItem(cartId = cart.uid, productId = productId) ?: return

        if (existing.quantity <= 1) {
            cartDao.deleteCartItem(cartId = cart.uid, productId = productId)
        } else {
            cartDao.updateCartItem(
                existing.copy(
                    quantity = existing.quantity - 1
                )
            )
        }
    }

    fun observeCartCount(cartId: Int): Flow<Int> {
        return cartDao.observeCartCount(cartId)
    }

    fun observeCartItem(cartId: Int, productId: Int): Flow<CartItem?> {
        return cartDao.observeCartItem(cartId, productId)
    }
}
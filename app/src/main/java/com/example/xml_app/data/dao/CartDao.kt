package com.example.xml_app.data.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import com.example.xml_app.entities.Cart
import com.example.xml_app.entities.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Insert
    suspend fun insert(cart: Cart): Long

    @Query("""select * from carts where user_id = :userId LIMIT 1""")
    suspend fun getCartByUserId(userId: Int): Cart?

    @Query("""select * from cart_items where cart_id = :cartId""")
    fun observeCartItems(cartId: Int): Flow<List<CartItem>>

    @Query("""select product_id, quantity from cart_items where cart_id = :cartId""")
    suspend fun getCartProductWithQuantity(cartId: Int): Map<Int, Int>

    @Query("""select * from cart_items where cart_id = :cartId and product_id = :productId LIMIT 1""")
    fun observeCartItem(cartId: Int, productId: Int): Flow<CartItem?>

    @Query("""select * from cart_items where cart_id = :cartId and product_id = :productId LIMIT 1 """)
    suspend fun getCartItem(cartId: Int, productId: Int): CartItem?

    @Insert
    suspend fun insertCartItem(item: CartItem)

    @Update
    suspend fun updateCartItem(item: CartItem)

    @Query("""delete from cart_items where cart_id = :cartId and product_id = :productId""")
    suspend fun deleteCartItem(cartId: Int, productId: Int)

    @Query("""select count(*) from cart_items where cart_id = :cartId""")
    fun observeCartCount(cartId: Int): Flow<Int>

    @Query("""delete from cart_items where cart_id = :cartId""")
    suspend fun clearCart(cartId: Int)

    @Query("""select product_id from cart_items where cart_id = :cartId""")
    suspend fun getAllProductsIdsInCart(cartId: Int): List<Int>
}
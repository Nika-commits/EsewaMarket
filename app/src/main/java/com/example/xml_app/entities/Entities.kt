package com.example.xml_app.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    indices = [
        Index(value = ["firebase_uid"], unique = true)
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "firebase_uid") val firebaseUid: String,
    @ColumnInfo(name = "full_name") val fullName: String,
    val username: String,
    val address: String,
    val phone: String
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["uid"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Cart(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "user_id", index = true) val userId: Int
)


@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Cart::class,
            parentColumns = ["uid"],
            childColumns = ["cart_id"],
            onDelete = ForeignKey.CASCADE
        ),

    ]
)
data class CartItem(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "cart_id", index = true) val cartId: Int,
    @ColumnInfo(name = "product_id") val productId: Int,
    val quantity: Int
)
package com.example.xml_app.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["firebase_uid"], unique = true)
    ]
)
data class User(
    @PrimaryKey(autoGenerate = false) val uid: Int,
    @ColumnInfo(name = "firebase_uid") val firebaseUid: String,
    @ColumnInfo(name = "full_name") val fullName: String,
    val username: String,
    val email: String
)

@Entity(
    tableName = "addresses",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["uid"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("user_id")
    ]
)
data class Address(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "phone_number") val phone: String,
    @ColumnInfo(name = "full_address") val fullAddress: String,
    @ColumnInfo(name = "label") val label: String = "Home",
    @ColumnInfo(name = "is_default_address") val isDefaultAddress: Boolean,
    @ColumnInfo(name = "is_default_shipping_address") val isDefaultShippingAddress: Boolean
)

@Entity(
    tableName = "carts",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["uid"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user_id"], unique = true)]
)
data class Cart(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int
)


@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = Cart::class,
            parentColumns = ["uid"],
            childColumns = ["cart_id"],
            onDelete = ForeignKey.CASCADE
        ),
    ],

    indices = [
        Index(value = ["cart_id", "product_id"], unique = true)
    ]
)
data class CartItem(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "cart_id", index = true) val cartId: Int,
    @ColumnInfo(name = "product_id") val productId: Int,
    val quantity: Int
)


@Entity(
    tableName = "favourites",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["uid"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("user_id"),
        Index(value = ["user_id", "product_id"], unique = true)
    ]
)
data class Favourite(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "product_id") val productId: Int
)
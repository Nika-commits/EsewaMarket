package com.example.xml_app.data

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.example.xml_app.data.dao.CartDao
import com.example.xml_app.data.dao.FavouriteDao
import com.example.xml_app.data.dao.UserDao
import com.example.xml_app.entities.Cart
import com.example.xml_app.entities.CartItem
import com.example.xml_app.entities.Favourite
import com.example.xml_app.entities.User

@Database(
    entities = [
        User::class,
        Cart::class,
        CartItem::class,
        Favourite::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun cartDao(): CartDao
    abstract fun favouriteDao(): FavouriteDao
}
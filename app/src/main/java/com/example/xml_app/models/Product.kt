package com.example.xml_app.models

data class Product(
    val id: Int,
    val name: String,
    val imageUrls: List<String>,
    val description: String,
    val price: Int,
    val status: String,
    val brand: String,
    val colors: List<Color>,
    val sizes: List<String>?,
//    val isFavourite: Boolean?,
//    val cartCont: Int?
)

data class Color(val name: String, val hexCode: String)

data class ProductUiModel(
    val product: Product,
    val isFavourite: Boolean,
    val cartCount: Int
)

data class ProductUiFavourite(
    val product: Product,
    val isOptionsRevealed: Boolean
)
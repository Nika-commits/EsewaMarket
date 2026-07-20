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
    val sizes: List<String>?
)

data class Color(val name: String, val hexCode: String)
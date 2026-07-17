package com.example.xml_app.Models

data class Product(
    val id: Int,
    val name: String,
    val imageUrls: List<String>,
    val description: String,
    val price: Int,
    val status: String,
    val brand: String,
    val colors: Colors,
    val sizes: List<String>
)

data class Colors(val name: String, val hexCode: String)
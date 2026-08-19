package com.example.xml_app.utils.dto

data class AddressResponseMapbox(
    val attribution: String,
    val features: List<Feature>,
    val query: List<Double>,
    val type: String
)
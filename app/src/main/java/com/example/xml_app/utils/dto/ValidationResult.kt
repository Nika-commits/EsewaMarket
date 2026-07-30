package com.example.xml_app.utils.dto

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)
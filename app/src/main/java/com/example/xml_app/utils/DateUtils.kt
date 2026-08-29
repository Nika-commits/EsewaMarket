package com.example.xml_app.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun String.formatOrderDate(): String {
    return try {
        Instant.parse(this)
            .atZone(ZoneId.systemDefault())
            .format(
                DateTimeFormatter.ofPattern(
                    "EEE MMM dd, yyyy",
                    Locale.ENGLISH
                )
            )
    } catch (e: Exception) {
        this
    }
}
package com.example.xml_app.models

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

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

class Color(val name: String, val hexCode: String)


@Serializable
data class ProductState(
    val isFavourite: Boolean = false,
    val cartCount: Int = 0
)

@Serializable
data class ProductStates(
    val products: Map<Int, ProductState> = emptyMap()
)

object ProductSerializer : Serializer<ProductStates> {
    override val defaultValue: ProductStates
        get() = ProductStates()

    override suspend fun readFrom(input: InputStream): ProductStates =
        try {
            Json.decodeFromString<ProductStates>(
                input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read", serialization)
        }

    override suspend fun writeTo(t: ProductStates, output: OutputStream) {
        output.write(
            Json.encodeToString(t).encodeToByteArray()
        )
    }
}


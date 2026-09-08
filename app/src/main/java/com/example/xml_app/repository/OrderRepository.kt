package com.example.xml_app.repository

import android.util.Log
import com.example.xml_app.BuildConfig
import com.example.xml_app.api.RetrofitInstance
import com.example.xml_app.utils.dto.request.CreateOrderRequest
import com.example.xml_app.utils.dto.request.OrderDateFilter
import com.example.xml_app.utils.dto.request.UpdateOrderPaymentStatusRequest
import com.example.xml_app.utils.dto.request.UpdateOrderStatusRequest
import com.example.xml_app.utils.dto.response.KhaltiPaymentResponse
import com.example.xml_app.utils.dto.response.KhaltiPaymentVerificationResponse
import com.example.xml_app.utils.dto.response.OrderResponse

class OrderRepository {

    suspend fun postOrder(
        token: String,
        request: CreateOrderRequest
    ) = RetrofitInstance.orderApi.postOrder(
        "Bearer $token",
        request
    )

    suspend fun getOrderById(
        id: Int,
        token: String
    ) = RetrofitInstance.orderApi.getOrderById(
        id,
        "Bearer $token"
    )

    suspend fun getOrders(
        token: String,
        status: String,
        dateFilter: OrderDateFilter? = null
    ) = RetrofitInstance.orderApi.getOrders(
        "Bearer $token",
        status = status,
        from = dateFilter?.from,
        to = dateFilter?.to
    )

    suspend fun updateOrderStatus(
        id: Int,
        token: String,
        request: UpdateOrderStatusRequest
    ) = RetrofitInstance.orderApi.updateOrderStatus(
        id = id,
        authorization = "Bearer $token",
        request = request
    )

    suspend fun updateOrderPaymentStatus(
        id: Int,
        token: String,
        request: UpdateOrderPaymentStatusRequest
    ): OrderResponse? {
        try {

            val response = RetrofitInstance.orderApi.updateOrderPaymentStatus(
                id = id,
                authorization = "Bearer $token",
                request = request
            )

            if (!response.isSuccessful) {
                Log.e("Order", "Failed to update payment status: ${response.code()}")
                return null
            }

            return response.body()
        } catch (e: Exception) {
            Log.e("Order", "Failed to update payment status: ${e.message}")
            return null
        }
    }

    suspend fun initiateKhaltiPayment(
        id: Int
    ): KhaltiPaymentResponse? {
        try {
            val khaltiLiveKey = BuildConfig.KhaltiLivePublic
            val response = RetrofitInstance.orderApi.initiateKhaltiPayment(
                id,
                authorization = "Bearer $khaltiLiveKey"
            )
            if (!response.isSuccessful) {
                Log.e("Khalti", "Error in Khalti Repository: ${response.code()}")
                return null
            }
            Log.d("Khalti", "${response.body()}")
            return response.body()
        } catch (e: Exception) {
            Log.e("Khalti", "Exception Occurred in Repo: ${e.message}")
            return null
        }
    }

    suspend fun verifyKhaltiPayment(
        pxid: String
    ): KhaltiPaymentVerificationResponse? {
        try {
            val response = RetrofitInstance.orderApi.verifyKhaltiPayment(
                pxid,

                )
            if (!response.isSuccessful) {
                Log.e("Khalti", "Khalti Verification Failed : ${response.code()}")
                return null
            }
            Log.d("Khalti", "${response.body()}")
            return response.body()
        } catch (e: Exception) {
            Log.e("Khalti", "Exception in Khalti Payment Verification: ${e.message}")
            return null
        }
    }
}

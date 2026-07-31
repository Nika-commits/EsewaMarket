package com.example.xml_app.utils.firebase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

val tag = "Auth"

object AuthRepository {
    private val auth: FirebaseAuth = Firebase.auth

    suspend fun login(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            Log.e(tag, "$e")
            false
        }
    }

    suspend fun register(email: String, password: String): Boolean {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            Log.e(tag, "$e")
            false
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun isAuth(): Boolean {
        val currentUser = auth.currentUser
        return currentUser != null
    }

    fun getUser(): FirebaseUser? {
        return auth.currentUser
    }

}
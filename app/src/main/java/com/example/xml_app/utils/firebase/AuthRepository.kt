package com.example.xml_app.utils.firebase

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

object AuthRepository {
    private val auth: FirebaseAuth = Firebase.auth

    fun login(email: String, password: String): Boolean {
        var result = false
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                result = task.isSuccessful
            }
        return result
    }

    fun register(email: String, password: String): Boolean {
        var result = false
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                result = task.isSuccessful
            }
        return result
    }

    fun isAuth(): Boolean {
        val currentUser = auth.currentUser
        return currentUser != null
    }

    fun getUser(): FirebaseUser? {
        return auth.currentUser
    }

}
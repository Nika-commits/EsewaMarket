package com.example.xml_app.utils.firebase

import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

val tag = "Auth"

object AuthRepository {
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore
        get() = Firebase.firestore

    suspend fun login(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            Log.e(tag, "$e")
            false
        }
    }

    suspend fun register(username: String, email: String, password: String): Boolean {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return false
            db.collection("users")
                .document(user.uid)
                .set(
                    mapOf(
                        "username" to username,
                        "email" to email
                    )
                ).await()
            true
        } catch (e: Exception) {
            Log.e(tag, "${e.message}")
            false
        }
    }

    suspend fun signInWithGoogle(credential: Credential): Boolean {
        return try {
            if (
                credential is CustomCredential &&
                credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val firebaseCredential =
                    GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                auth.signInWithCredential(firebaseCredential).await()
                true
            } else {
                Log.w(tag, "Invalid Credentials")
                false
            }
        } catch (e: Exception) {
            Log.e(tag, "${e.message}")
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
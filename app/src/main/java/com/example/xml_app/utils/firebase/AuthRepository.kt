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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

const val TAG = "Auth"


object AuthRepository {
    //    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore
        get() = Firebase.firestore

    suspend fun login(email: String, password: String, auth: FirebaseAuth): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "$e")
            false
        }
    }

    suspend fun register(
        username: String,
        email: String,
        password: String,
        auth: FirebaseAuth
    ): Boolean {
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
            Log.e(TAG, "${e.message}")
            false
        }
    }

    suspend fun signInWithGoogle(credential: Credential, auth: FirebaseAuth): Boolean {
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
                Log.w(TAG, "Invalid Credentials")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
            false
        }
    }


    fun logout(auth: FirebaseAuth) {
        auth.signOut()
    }

    fun isAuth(auth: FirebaseAuth): Boolean {
        val currentUser = auth.currentUser
        return currentUser != null
    }

    fun getUser(auth: FirebaseAuth): FirebaseUser? {
        return auth.currentUser
    }

}
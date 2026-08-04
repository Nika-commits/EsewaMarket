package com.example.xml_app.utils.firebase

import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

const val TAG = "Auth"

object AuthRepository {
    suspend fun login(email: String, password: String, auth: FirebaseAuth): FirebaseUser? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            Log.e(TAG, "$e")
            null
        }
    }

    suspend fun register(
        email: String,
        password: String,
        auth: FirebaseAuth,
    ): FirebaseUser? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
            null
        }
    }

    suspend fun signInWithGoogle(credential: Credential, auth: FirebaseAuth): FirebaseUser? {
        return try {
            if (
                credential is CustomCredential &&
                credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val firebaseCredential =
                    GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val result = auth.signInWithCredential(firebaseCredential).await()
                result.user
            } else {
                Log.w(TAG, "Invalid Credentials")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
            null
        }
    }

    fun logout(auth: FirebaseAuth) {
        auth.signOut()
    }

//    fun isAuth(auth: FirebaseAuth): Boolean {
//        val currentUser = auth.currentUser
//        return currentUser != null
//    }

    fun getUser(auth: FirebaseAuth): FirebaseUser? {
        return auth.currentUser
    }

}
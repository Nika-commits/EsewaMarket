package com.example.xml_app.utils

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class CustomApplicationContext : Application() {
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore

    override fun onCreate() {
        super.onCreate()
        auth = Firebase.auth
        db = Firebase.firestore
    }
}
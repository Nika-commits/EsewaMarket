package com.example.xml_app.utils

import android.app.Application
import androidx.room3.Room
import com.example.xml_app.data.AppDatabase
import com.example.xml_app.data.MIGRATION2_3
import com.example.xml_app.data.MIGRATION_1_2
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import org.koin.core.context.startKoin

class CustomApplicationContext : Application() {
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        auth = Firebase.auth
        db = Firebase.firestore

        database = Room.databaseBuilder(
            context = applicationContext,
            name = "app_database",
            klass = AppDatabase::class.java
        )
            .addMigrations(MIGRATION_1_2, MIGRATION2_3)
            .build()

        startKoin {
        }

    }


}
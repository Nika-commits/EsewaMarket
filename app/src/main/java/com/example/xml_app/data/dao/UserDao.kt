package com.example.xml_app.data.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.example.xml_app.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Upsert
    suspend fun upsert(user: User)

    @Query("""select * from users where firebase_uid = :firebaseUid LIMIT 1""")
    suspend fun getFirebaseUserById(firebaseUid: String): User?

    @Query("""select * from users where firebase_uid = :firebaseUid LIMIT 1""")
    fun observeByFirebaseUid(firebaseUid: String): Flow<User>
}
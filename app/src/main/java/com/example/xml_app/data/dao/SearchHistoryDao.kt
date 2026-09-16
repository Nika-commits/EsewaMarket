package com.example.xml_app.data.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.example.xml_app.entities.SearchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Insert
    suspend fun insert(searchHistory: SearchHistory)

    @Query(
        """
            update search_history
            set searched_at = :searchedAt
            where user_id = :userId
            and query = :query
        """
    )
    suspend fun updateSearchTime(
        userId: Int,
        query: String,
        searchedAt: Long
    ): Int

    @Query(
        """select * 
        from search_history 
        where user_id = :userId
        order by searched_at desc
        """
    )
    fun getUserSearchHistory(userId: Int): Flow<List<SearchHistory>>

    @Query("delete from search_history where user_id = :userId and uid = :searchId")
    suspend fun deleteSearch(userId: Int, searchId: Int)

    @Query("delete from search_history where user_id = :userId")
    suspend fun clearUserSearchHistory(userId: Int)
}
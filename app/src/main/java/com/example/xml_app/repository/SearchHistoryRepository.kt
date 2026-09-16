package com.example.xml_app.repository

import com.example.xml_app.data.dao.SearchHistoryDao
import com.example.xml_app.entities.SearchHistory
import kotlinx.coroutines.flow.Flow

class SearchHistoryRepository(
    private val searchHistoryDao: SearchHistoryDao
) {

    fun getSearchHistories(userId: Int): Flow<List<SearchHistory>> {
        return searchHistoryDao.getUserSearchHistory(userId)
    }

    suspend fun saveSearch(
        userId: Int,
        query: String
    ) {
        val updated = searchHistoryDao
            .updateSearchTime(userId, query, System.currentTimeMillis())
        if (updated == 0) {
            searchHistoryDao.insert(
                SearchHistory(
                    userId = userId,
                    query = query
                )
            )
        }
    }

    suspend fun deleteSearch(
        userId: Int,
        searchHistoryId: Int
    ) {
        return searchHistoryDao.deleteSearch(
            userId = userId,
            searchId = searchHistoryId
        )
    }

    suspend fun clearSearch(
        userId: Int,
    ) {
        return searchHistoryDao.clearUserSearchHistory(userId)
    }

}
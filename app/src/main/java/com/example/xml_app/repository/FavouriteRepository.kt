package com.example.xml_app.repository

import com.example.xml_app.data.dao.FavouriteDao
import com.example.xml_app.entities.Favourite
import kotlinx.coroutines.flow.Flow

class FavouriteRepository(
    private val favouriteDao: FavouriteDao
) {
    fun observeFavouriteIds(userId: Int): Flow<List<Int>> {
        return favouriteDao.observeFavouriteProducts(userId)
    }

    suspend fun toggleFavourite(userId: Int, productId: Int) {
        if (favouriteDao.isFavourite(userId, productId)) {
            favouriteDao.delete(userId, productId)
        } else {
            favouriteDao.insert(Favourite(userId = userId, productId = productId))
        }
    }

    suspend fun observeFavouriteCount(userId: Int): Flow<Int> {
        return favouriteDao.observeFavouritesCount(userId)
    }
}
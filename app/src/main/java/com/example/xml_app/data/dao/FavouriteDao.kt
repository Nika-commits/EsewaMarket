package com.example.xml_app.data.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.example.xml_app.entities.Favourite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDao {
    @Insert
    suspend fun insert(favourite: Favourite)

    @Query("""delete from favourites where user_id = :userId and product_id = :productId""")
    suspend fun delete(userId: Int, productId: Int)

    @Query("""select Exists(select 1 from favourites where user_id = :userId and product_id = :productId)""")
    suspend fun isFavourite(userId: Int, productId: Int): Boolean

    @Query("""select product_id from favourites where user_id = :userId""")
    fun observeFavouriteProducts(userId: Int): Flow<List<Int>>

    @Query("""select count(*) from favourites where user_id = :userId""")
    fun observeFavouritesCount(userId: Int): Flow<Int>
}
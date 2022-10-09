package com.example.favdish.model.database

import androidx.room.*
import com.example.favdish.model.entity.FavDish
import kotlinx.coroutines.flow.Flow

@Dao
interface FavDishDao {

    @Insert
    suspend fun insertFavDishDetails(favDish: FavDish)

    @Query("SELECT * FROM FAV_DISHES_TABLE ORDER BY ID")
    fun getAllDishesList() : Flow<List<FavDish>>

    @Update
    suspend fun updateFavDishDetails(favDish: FavDish)

    @Query("SELECT * FROM FAV_DISHES_TABLE WHERE favorite_dish = 1")
    fun getFavDishList() : Flow<List<FavDish>>

    @Delete
    suspend fun deleteDish(dish: FavDish)

    @Query("SELECT * FROM FAV_DISHES_TABLE WHERE TYPE = :filterType")
    fun getFilterDishesList(filterType: String): Flow<List<FavDish>>

}
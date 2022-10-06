package com.example.favdish.model.database

import androidx.annotation.WorkerThread
import com.example.favdish.model.entity.FavDish

class FavDishRepository(private val favDishDao: FavDishDao) {

    @WorkerThread
    suspend fun insertFavDishData(favDish: FavDish){
        favDishDao.insertFavDishDetails(favDish)
    }

}
package com.example.favdish.viewModel

import androidx.lifecycle.*
import com.example.favdish.model.database.FavDishRepository
import com.example.favdish.model.entity.FavDish
import kotlinx.coroutines.launch

class FavDishViewModel(private val repository: FavDishRepository) : ViewModel() {

    fun insert(dish: FavDish) = viewModelScope.launch {
        repository.insertFavDishData(dish)
    }

    val allDishesList: LiveData<List<FavDish>> = repository.allDishesList.asLiveData()

    fun update(dish: FavDish) = viewModelScope.launch {
        repository.updateFavDishData(dish)
    }

    val favDishesList: LiveData<List<FavDish>> = repository.favDishesList.asLiveData()

    fun delete(dish: FavDish) = viewModelScope.launch {
        repository.deleteDishData(dish)
    }

    fun getFilteredList(value: String): LiveData<List<FavDish>> =
        repository.filterDishesList(value).asLiveData()

}

@Suppress("UNCHECKED_CAST")
class FavDishViewModelFactory(private val repository: FavDishRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(FavDishViewModel::class.java)) {
            return FavDishViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")

    }
}
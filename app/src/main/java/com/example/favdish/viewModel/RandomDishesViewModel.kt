package com.example.favdish.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.favdish.model.entity.RandomDish
import com.example.favdish.model.network.RandomDishAPIService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class RandomDishesViewModel : ViewModel() {

    private val randomRecipeAPIService = RandomDishAPIService()

    private val compositeDisposable = CompositeDisposable()

    val loadRandomDish = MutableLiveData<Boolean>()
    val randomDishResponse = MutableLiveData<RandomDish.Recipes>()
    val randomDishLoadingError = MutableLiveData<Boolean>()

    fun getRandomRecipeFromAPI(){
        loadRandomDish.value = true
        compositeDisposable.add(
            randomRecipeAPIService.getRandomDish()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object :DisposableSingleObserver<RandomDish.Recipes>(){
                    override fun onSuccess(value: RandomDish.Recipes) {
                        loadRandomDish.value = true
                        randomDishResponse.value = value
                        randomDishLoadingError.value = false
                    }

                    override fun onError(e: Throwable) {
                        loadRandomDish.value = false
                        randomDishLoadingError.value = true
                        Log.d("error", e.toString())
                        e.printStackTrace()
                    }

                })
        )
    }

}
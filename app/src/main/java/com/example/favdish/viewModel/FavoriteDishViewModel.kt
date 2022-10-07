package com.example.favdish.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoriteDishViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is favorite dish Fragment"
    }
    val text: LiveData<String> = _text
}
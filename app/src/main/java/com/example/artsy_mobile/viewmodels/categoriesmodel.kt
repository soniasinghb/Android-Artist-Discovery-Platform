package com.example.artsy_mobile.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artsy_mobile.interfaces.Categories
import com.example.artsy_mobile.interfaces.CategoriesInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class categoriesmodel: ViewModel() {
    private val _categoriesDeets = MutableStateFlow<List<Categories>>(emptyList())
    val categoriesDeets : MutableStateFlow<List<Categories>> = _categoriesDeets

    private val _isLoading = MutableStateFlow(false)
    val isLoading : MutableStateFlow<Boolean> = _isLoading

    fun getCategories(artworkId: String){
        viewModelScope.launch {
            _isLoading.value = true
            val res = CategoriesInstance.api.fetchCategories(artworkId)
            if(res.isSuccessful){
                _categoriesDeets.value = res.body()?.message ?: emptyList()
                _isLoading.value = false
                Log.d("Categories", "Success: ${res.body()?.message}")
            }
            else{
                _categoriesDeets.value = emptyList()
                _isLoading.value = false
                Log.d("Categories", "Error: ${res.body()?.message}")
            }
        }
    }
}
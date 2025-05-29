package com.example.artsy_mobile.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artsy_mobile.interfaces.ArtistDetails
import com.example.artsy_mobile.interfaces.ArtistDetailsInstance
import com.example.artsy_mobile.interfaces.ArtistDetailsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class artistdetailmodel : ViewModel(){
    private val _artistDeets = MutableStateFlow<ArtistDetails?>(null)
    val artistDeets: StateFlow<ArtistDetails?> = _artistDeets

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchArtistDeets(artistId: String){
        viewModelScope.launch {
            _isLoading.value = true
            val res = ArtistDetailsInstance.api.fetchArtistDetails(artistId)
            if(res.isSuccessful){
//                val artisttdeet = res.body()?.
                _artistDeets.value = res.body()?.message
                _isLoading.value = false
            }
            else{
                _artistDeets.value = null
                _isLoading.value = false
            }
            _isLoading.value = false
        }
    }
}
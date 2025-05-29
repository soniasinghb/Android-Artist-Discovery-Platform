package com.example.artsy_mobile.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.artsy_mobile.interfaces.ArtistSearchInstance
import com.example.artsy_mobile.interfaces.ArtistSearchResults
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class Searchmodel: ViewModel() {
    private val _searchArtist = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchArtist.asStateFlow()

    private val _searchRes = MutableStateFlow<List<ArtistSearchResults>>(emptyList())
    val searchRes: StateFlow<List<ArtistSearchResults>> = _searchRes.asStateFlow()

    @UnstableApi
    fun updateArtistQuery(artistName: String) {
        _searchArtist.value = artistName
        if(_searchArtist.value.length >= 3){
            searchArtist(artistName)
        }else{
            _searchRes.value = emptyList()
        }
    }
    fun clearSearch(){
        _searchArtist.value = ""
        _searchRes.value = emptyList()
    }

    @UnstableApi
    fun searchArtist(artistName: String){
        viewModelScope.launch {
            try {
                val res = ArtistSearchInstance.api.fetchArtists(artistName)
                if(res.isSuccessful){
                    res.body()?.let { artistsResponse ->
                        _searchRes.value = artistsResponse.message
                        Log.d("SearchModel", "Response received: ${artistsResponse.message}")
                    }?: run{
                        _searchRes.value = emptyList()
                    }
                }
                else{
                    Log.d("SearchModel", "Response failed: ${res.message()}")
                    _searchRes.value = emptyList()
                }
            }
            catch (e: Exception){
                Log.d("SearchModel", "Exception: ${e.message}")
                _searchRes.value = emptyList()
            }
        }
    }
}
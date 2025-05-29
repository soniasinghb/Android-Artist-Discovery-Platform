package com.example.artsy_mobile.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artsy_mobile.interfaces.ArtworkDetails
import com.example.artsy_mobile.interfaces.ArtworksInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class artworksmodel: ViewModel() {
    private val _artworks = MutableStateFlow<List<ArtworkDetails>>(emptyList())
    val artworks: StateFlow<List<ArtworkDetails>> = _artworks.asStateFlow()

    fun fetchArtworks(artistId: String) {
        viewModelScope.launch {
            val res = ArtworksInstance.api.getArtworks(artistId)
            if (res.isSuccessful) {
                _artworks.value = res.body()?.message ?: emptyList()
            } else {
                _artworks.value = emptyList()
            }
        }
    }
}
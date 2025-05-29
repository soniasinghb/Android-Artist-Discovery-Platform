package com.example.artsy_mobile.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artsy_mobile.interfaces.ArtistSearchResults
import com.example.artsy_mobile.interfaces.ArtworkDetails
import com.example.artsy_mobile.interfaces.SimArtistsInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class simartistmodel : ViewModel() {
    private val _simArtDeets = MutableStateFlow<List<ArtistSearchResults>>(emptyList())
    val simArtDeets: MutableStateFlow<List<ArtistSearchResults>> = _simArtDeets

    fun fetchSimArtists(artistId: String) {
        viewModelScope.launch {
            val res = SimArtistsInstance.api.fetchSimilarArtists(artistId)
            if (res.isSuccessful) {
                _simArtDeets.value = res.body()?.message ?: emptyList()
            } else {
                _simArtDeets.value = emptyList()
            }
        }
    }
}
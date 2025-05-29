package com.example.artsy_mobile.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artsy_mobile.interfaces.ArtistSearchResults
import com.example.artsy_mobile.interfaces.DeleteAccInterface
import com.example.artsy_mobile.interfaces.LoginInterface
import com.example.artsy_mobile.interfaces.LoginReq
import com.example.artsy_mobile.interfaces.LogoutInterface
import com.example.artsy_mobile.interfaces.MeInterface
import com.example.artsy_mobile.interfaces.RegisterInterface
import com.example.artsy_mobile.interfaces.RegisterReq
import com.example.artsy_mobile.interfaces.RemoveFavReq
import com.example.artsy_mobile.interfaces.artistDetailsInterface
import com.example.artsy_mobile.interfaces.favorite
import com.example.artsy_mobile.interfaces.favsInterface
import com.example.artsy_mobile.interfaces.userProfile
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date

class UserAuth(application: Application) : AndroidViewModel(application){
    // You can use mutableStateOf to create an observable state in Compose
    var isLoggedIn = mutableStateOf(false)
    var userDeets = mutableStateOf<userProfile?>(null)
    var favorites = mutableStateOf<List<favorite>?>(listOf())  //make string into artist object by writing a structure/data class

    private val _isRegLoading = MutableStateFlow(false)
    val isRegLoading: StateFlow<Boolean> = _isRegLoading

    private val _isLoginLoading = MutableStateFlow(false)
    val isLoginLoading: StateFlow<Boolean> = _isLoginLoading

    private val retrofit: Retrofit
    private val cookieJar: ClearableCookieJar
    init {
        val(cookieJ, client) = createInstance(application.applicationContext)
        cookieJar = cookieJ
        retrofit = client
    }

    fun register(
        email: String,
        password: String,
        fullname: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ){
        viewModelScope.launch {
            _isRegLoading.value = true
            val authService = retrofit.create(RegisterInterface::class.java)
            val response = authService.register(RegisterReq(email, password, fullname))
            if(response.isSuccessful && response.body()?.result==true){
                _isRegLoading.value = false
                isLoggedIn.value=true
                onSuccess()
                //get favs??????????
            }else if(response.isSuccessful && response.body()?.result==false){
                onFailure(response.body()?.message ?: "Registration failed in fetch")
                _isRegLoading.value = false
            }
            else{
                onFailure("Registration Call failed")
                _isRegLoading.value = false
            }
        }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ){
        viewModelScope.launch {
            _isLoginLoading.value = true
            val authService = retrofit.create(LoginInterface::class.java)
            val response = authService.login(LoginReq(email, password))
            if(response.isSuccessful && response.body()?.result==true){
                _isLoginLoading.value = false
                isLoggedIn.value=true
                onSuccess()
            }else if(response.isSuccessful && response.body()?.result==false){
                _isLoginLoading.value = false
                onFailure(response.body()?.message ?: "Login failed in fetch")
            }
            else{
                _isLoginLoading.value = false
                onFailure("Login Call failed")
            }
        }
    }

    fun createInstance(context: Context): Pair<ClearableCookieJar, Retrofit> {
        val cookieJar: ClearableCookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context))
        val build = OkHttpClient.Builder().cookieJar(cookieJar).build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://android-backend-final.uw.r.appspot.com/")
            .client(build)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return Pair(cookieJar, retrofit)
    }

    fun getMe(){
        viewModelScope.launch {
            val authService = retrofit.create(MeInterface::class.java)
            val response = authService.fetchMe()
            if(response.isSuccessful && response.body()?.result==true){
//                onSuccess()
                Log.d("ME_RESPONSE", response.body().toString())
                isLoggedIn.value=true
                userDeets.value = response.body()?.message
                favorites.value = response.body()?.message?.favArtists
            }
            else if(response.isSuccessful && response.body()?.result==false){
                Log.d("ME_RESPONSE", response.body().toString())
//                onFailure("User Unauthenticated")
            }
            else{
                Log.d("ME_RESPONSE", "call fail")
//                onFailure("Login Call failed")
            }
        }
    }

    fun logout(onSuccess: () -> Unit, onFailure: (String) -> Unit){
        viewModelScope.launch {
            val authService = retrofit.create(LogoutInterface::class.java)
            val response = authService.logout()
            if(response.isSuccessful && response.body()?.result==true){
                isLoggedIn.value=false
                cookieJar.clear()
                userDeets.value = null
                favorites.value = emptyList()
                onSuccess()
            }
            else if(response.isSuccessful && response.body()?.result==false){
                onFailure(response.body()?.message ?: "Logout failed in fetch")
            }
            else{
                Log.d("LOGOUT_RESPONSE", "${ response.body()?.message }")
            }
        }
    }

    fun deleteAccount(onSuccess: () -> Unit, onFailure: (String) -> Unit){
        viewModelScope.launch {
            val authService = retrofit.create(DeleteAccInterface::class.java)
            val response = authService.deleteAcc()
            if(response.isSuccessful && response.body()?.result==true){
                isLoggedIn.value=false
                cookieJar.clear()
                userDeets.value = null
                favorites.value = emptyList()
                onSuccess()
                Log.d("DELETE_RESPONSE", "deleted")
            }
            else if(response.isSuccessful && response.body()?.result==false){
                onFailure(response.body()?.message ?: "Delete Account failed in fetch")
                Log.d("DELETE_RESPONSE", "delete failed")
            }
            else{
                onFailure("Delete Account Call failed")
                Log.d("DELETE_RESPONSE", "call fail")
            }
        }
    }

    fun toggleFavs(
        fav: ArtistSearchResults
    ){
        viewModelScope.launch {
            val authService = retrofit.create(favsInterface::class.java)
            val isFav = favorites.value?.any { it.artistId == fav.artist_ID } ?: false

            if (isFav) {
                val response = authService.removeFav(RemoveFavReq(fav.artist_ID))
                if(response.isSuccessful && response.body()?.result==true){
                    favorites.value = favorites.value?.filter { it.artistId != fav.artist_ID }
                    Log.d("DELETE_RESPONSE", "success deleted")
                }else{
                    Log.d("DELETE_RESPONSE", response.body().toString())
                }
            }else{
                val getDetailsApi = retrofit.create(artistDetailsInterface::class.java)
                val deetss = getDetailsApi.fetchArtistDetails(fav.artist_ID)
                if((deetss.isSuccessful)&&(deetss.body()?.result==true)){
                   val favDeets = deetss.body()?.message
                   val favArtistDeet =
                       favorite(
                           artistId = favDeets?.artist_id ?: "",
                           artistName = favDeets?.artist_name ?: "",
                           artistImg = "",
                           artistBday = favDeets?.artist_bday ?: "",
                           artistDday = favDeets?.artist_dday?: "",
                           artistNationality = favDeets?.artist_nationality?: "",
                           additionTime = Date()
                       )

                    val response = authService.addFav(favArtistDeet)
                    if(response.isSuccessful && response.body()?.result==true){
                        favorites.value = favorites.value?.plus(favArtistDeet)
                        Log.d("ADDFAV_RESPONSE", "success added")
                    }
                    else{
                        Log.d("ADDFAV_RESPONSE", response.body().toString())
                    }
                }
            }

        }
    }

}
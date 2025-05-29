package com.example.artsy_mobile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.artsy_mobile.Screen
import com.example.artsy_mobile.screens.Details
import com.example.artsy_mobile.screens.HomeScreen
import com.example.artsy_mobile.screens.Login
import com.example.artsy_mobile.screens.Register
import com.example.artsy_mobile.screens.Search
import com.example.artsy_mobile.viewmodels.Searchmodel
import com.example.artsy_mobile.viewmodels.UserAuth

@UnstableApi
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(){
    val navController = rememberNavController()

    val authModel: UserAuth = viewModel()

    NavHost(navController = navController,
        startDestination = Screen.HomeScreen.route
    ){
        composable(route = Screen.HomeScreen.route){
            HomeScreen(navController = navController, authModel)
        }
        composable(route = Screen.Login.route){
            Login(navController = navController, authModel)
        }
        composable(route = Screen.Register.route){
            Register(navController = navController, authModel)
        }
        composable(route = Screen.searchArtists.route){
            val searchmodel: Searchmodel = viewModel()
            Search(navController = navController, searchModel = searchmodel, authModel)
        }
        composable(route = Screen.artistDetails.route,
            arguments = listOf(
                navArgument("artistId"){
                    type = NavType.StringType
                },
                navArgument("artistName"){
                    type = NavType.StringType
                }
        )){ backStackEntry ->
            val artistId = backStackEntry.arguments?.getString("artistId") ?: ""
            val artistName = backStackEntry.arguments?.getString("artistName") ?: ""
            Details(artistId = artistId, artistName = artistName, navController = navController, authModel)
        }
    }
}
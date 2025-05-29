package com.example.artsy_mobile.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.artsy_mobile.viewmodels.Searchmodel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import coil3.compose.rememberAsyncImagePainter
import com.example.artsy_mobile.R
import com.example.artsy_mobile.Screen
import com.example.artsy_mobile.interfaces.ArtistSearchResults
import com.example.artsy_mobile.ui.theme.shadeColor
import com.example.artsy_mobile.ui.theme.topBarColor
import com.example.artsy_mobile.viewmodels.UserAuth
import kotlinx.coroutines.launch

@UnstableApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(navController: NavHostController, searchModel: Searchmodel, authModel : UserAuth){
    val searchQuery by searchModel.searchQuery.collectAsState()
    val searchRes by searchModel.searchRes.collectAsState()
    var active by remember { mutableStateOf(true) }
    val snackbarHostStat = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostStat) },
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = topBarColor()
                ),
                title = {
                    TextField(
                        value         = searchQuery,
                        onValueChange = searchModel::updateArtistQuery,
                        singleLine    = true,
                        placeholder   = { Text("Search artists…", fontSize = 19.sp) },
                        modifier      = Modifier.fillMaxWidth(),
                        colors        = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {  }) {
                        Icon(Icons.Default.Search, null)
                    }
                },
                actions = {
                    IconButton(onClick = { searchModel.clearSearch()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Close, null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(Modifier
            .padding(innerPadding)
            .padding(0.dp)) {
            if (searchRes.isNotEmpty()) {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(searchRes) { artist ->
                        ArtistCard(
//                        imageUrl = artist.artist_Image,
//                        name = artist.artist_Title,
                            artist = artist,
                            authModel = authModel,
                            onClick = {
                                navController.navigate(
                                    Screen.artistDetails.createRoute(
                                        artist.artist_ID,
                                        artist.artist_Title
                                    )
                                )
                            },
                            showSnackbarmsg = { msg ->
                                scope.launch { snackbarHostStat.showSnackbar(msg) }
                            }
                        )
                    }
                }
            }
            else if(searchQuery.length>=3){
                Box(modifier = Modifier
                    .fillMaxWidth(fraction = 0.95f)
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(15.dp))
                    .background(topBarColor()),
                    contentAlignment = Alignment.Center
                ){
                    Text(text = "No Result Found",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ArtistCard(
//    imageUrl: String,
//    name: String,
    artist: ArtistSearchResults,
    authModel: UserAuth,
    onClick: () -> Unit,
    showSnackbarmsg: (String) -> Unit
){
//    val isFav = authModel.favorites.value?.any { it.artistId == artist.artist_ID } ==true

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
        ,
        shape = RoundedCornerShape(16.dp),
    ) {
        val painter = if (artist.artist_Image == "/artsy_logo.svg") {
            painterResource(id = R.drawable.artsy_logo)
            //full thing should fit in...
        } else {
            rememberAsyncImagePainter(model = artist.artist_Image)
        }
        Box(){
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(195.dp),
                contentScale = ContentScale.Crop
            )
            if(authModel.isLoggedIn.value) {
                IconButton(
                    onClick = {
                        authModel.toggleFavs(artist)
                        if (authModel.favorites.value?.any { it.artistId == artist.artist_ID } == true) {
                            showSnackbarmsg("Removed from favorites")
                        } else {
                            showSnackbarmsg("Added to favorites")
                        }
                  },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(
                            color = topBarColor(),
                            shape = CircleShape
                        )
                ) {
                    if(authModel.favorites.value?.any { it.artistId == artist.artist_ID } ==true) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "star filled",
                        )
                    }
                    else{
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_star_outline_24),
                            contentDescription = "star unfilled",
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(shadeColor())
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 16.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = artist.artist_Title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "next icon",
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
        }
    }
}
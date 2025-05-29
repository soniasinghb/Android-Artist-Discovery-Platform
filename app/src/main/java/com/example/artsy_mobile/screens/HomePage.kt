package com.example.artsy_mobile.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.example.artsy_mobile.Screen
import com.example.artsy_mobile.interfaces.favorite
import com.example.artsy_mobile.ui.theme.favTextColor
import com.example.artsy_mobile.ui.theme.topBarColor
import com.example.artsy_mobile.viewmodels.UserAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.exp

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavHostController, userViewModel: UserAuth ){

    val snackBarHostStat = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        userViewModel.getMe()
    }
    val currDate = remember { LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}
    Scaffold(topBar = {NavBar(navController = navController, userViewModel = userViewModel,
        showSnackbar = { message ->
            coroutineScope.launch {
                snackBarHostStat.showSnackbar(message)
            }
        })},
        snackbarHost = { SnackbarHost(snackBarHostStat) })
    {innerPadding->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
        {
            Text(
                text = currDate,
                modifier = Modifier.padding(12.dp),
                color = Color(0xFF505056)
            )
            Box(
                modifier = Modifier
                    .background(favTextColor())
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center

            ){
            Text(
                text = "Favorites",
                modifier = Modifier.padding(3.dp),
                fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Favorites(
                isLoggedIn = userViewModel.isLoggedIn.value,
                favorites = userViewModel.favorites.value,
                clickLogin = { navController.navigate(Screen.Login.route) },
                navController = navController
            )
            Spacer(modifier = Modifier.height(19.dp))
            Box(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ){
                ArtsyPage()
            }
        }
    }
}

@Composable
fun Favorites(isLoggedIn: Boolean, favorites: List<favorite>?, clickLogin: () -> Unit, navController: NavHostController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (!isLoggedIn) {
            Button(
                onClick = clickLogin ,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Log in to see favorites")
            }
        } else {
            if(favorites?.isEmpty()==true){
                Box(modifier = Modifier
                    .fillMaxWidth(fraction = 0.95f)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(15.dp))
                    .background(topBarColor()),
                    contentAlignment = Alignment.Center
                    ){
                    Text(text = "No favorites",
                    modifier = Modifier.padding(16.dp)
                    )
                }
            }
            else{
                showFavs(favorites, navController)
            }
        }
    }
}

@Composable
fun showFavs(favorites: List<favorite>?, navController: NavHostController) {
    val now = remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (isActive) {
            now.value = System.currentTimeMillis()
            delay(1_000)
        }
    }
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(favorites?: emptyList()) { artist ->
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp)
////            .clickable(onClick = onClick)
//            ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
//                            .clickable { onClick() }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(Modifier.weight(1f)) {
                            Text(text = artist.artistName, fontSize = 19.sp)
                            Text(text = "${ artist.artistNationality }, ${ artist.artistBday }", fontSize = 12.sp)
                        }
                        val timediff = (now.value - artist.additionTime.time)/1000
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(TimeFormatter(timediff), fontSize = 12.sp)
                            IconButton(onClick = {
                                navController.navigate(
                                    Screen.artistDetails.createRoute(
                                        artist.artistId,
                                        artist.artistName
                                    )
                                )
                            }) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "next icon",
                        )}
                        }
                }
        }
    }
}

fun TimeFormatter(time: Long): String {
    if(time<2) return "1 second ago"
    else if(time<60) return "$time seconds ago"
    else if(time<120) return "1 minute ago"
    else if(time<3600) return "${time/60} minutes ago"
    else if(time<7200) return "1 hour ago"
    else if(time<86400) return "${time/3600} hours ago"
    else if(time<172800) return "1 day ago"
    else return "${time/86400} days ago"
}

@Composable
fun ArtsyPage() {
    val context = LocalContext.current
    val artsyUri = "https://www.artsy.net/"
    val displayText = "Powered by Artsy"
    ClickableText(
        text = AnnotatedString(displayText,
            spanStyle = SpanStyle(
                fontStyle = FontStyle.Italic,
                color = Color(0xFF626265)
            )
        ),
        onClick = {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, artsyUri.toUri())
            )
        }
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavBar(navController: NavHostController, userViewModel: UserAuth, showSnackbar: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
        TopAppBar(
            title = { Text(text = "Artist Search") },
            modifier = Modifier.fillMaxWidth(),
            actions = {
                IconButton(onClick = { navController.navigate(Screen.searchArtists.route) }) {
                    Icon(Icons.Outlined.Search, contentDescription = "Search")
                }

                IconButton(onClick = {  }) {
                    if (userViewModel.isLoggedIn.value) {
//                        Log.d("user", userViewModel.userDeets.value.toString())
                        IconButton(onClick = { expanded = !expanded }) {
                            Image(
                                painter = rememberAsyncImagePainter(userViewModel.userDeets.value?.profileImageUrl),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(19.dp)
                                    .width(19.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(
                                text = { Text("Log Out") },
                                onClick = {
                                    userViewModel.logout(onSuccess = {
                                        showSnackbar("Logged out successfully")
                                        navController.navigate(Screen.HomeScreen.route)
                                    }) { }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete Account", color = Color(0xFFAD0101)) },
                                onClick = {
                                    userViewModel.deleteAccount(onSuccess = {
                                        showSnackbar("Deleted user successfully")
                                        navController.navigate(Screen.HomeScreen.route)
                                    }) { }
                                }
                            )
                        }
                    } else
                        IconButton(onClick = { navController.navigate(Screen.Login.route) }) {
                            Icon(Icons.Outlined.Person, contentDescription = "Profile")
                        }
                }
            },
            colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                containerColor = topBarColor()
            )
        )

}
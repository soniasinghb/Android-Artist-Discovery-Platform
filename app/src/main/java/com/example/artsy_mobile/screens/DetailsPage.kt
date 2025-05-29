package com.example.artsy_mobile.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import android.net.Uri
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import com.example.artsy_mobile.R
import com.example.artsy_mobile.Screen
import com.example.artsy_mobile.interfaces.ArtistDetails
import com.example.artsy_mobile.interfaces.ArtistDetailsInstance
import com.example.artsy_mobile.interfaces.ArtistSearchResults
import com.example.artsy_mobile.interfaces.ArtworkDetails
import com.example.artsy_mobile.interfaces.Categories
import com.example.artsy_mobile.viewmodels.UserAuth
import com.example.artsy_mobile.viewmodels.artistdetailmodel
import com.example.artsy_mobile.viewmodels.artworksmodel
import com.example.artsy_mobile.viewmodels.categoriesmodel
import com.example.artsy_mobile.viewmodels.simartistmodel
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.example.artsy_mobile.ui.theme.topBarColor

@Composable
fun Details(artistId: String, artistName: String, navController: NavHostController, authModel: UserAuth){
    val detailsviewModel : artistdetailmodel = viewModel()
    val artworksViewModel: artworksmodel = viewModel()
    val simArtistViewModel: simartistmodel = viewModel()

    val artistDeets by detailsviewModel.artistDeets.collectAsState()
    val artworkDeets by artworksViewModel.artworks.collectAsState()
    val simArtistDeets by simArtistViewModel.simArtDeets.collectAsState()
    val isLoading by detailsviewModel.isLoading.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(artistId) {
        detailsviewModel.fetchArtistDeets(artistId)
        artworksViewModel.fetchArtworks(artistId)
        simArtistViewModel.fetchSimArtists(artistId)
    }

    var currTab by remember { mutableStateOf(0) }

    var tabs = listOf("Details", "Artworks")
    var icons = listOf(Icons.Outlined.Info, Icons.Outlined.AccountBox)
    if(authModel.isLoggedIn.value == true){
        tabs = listOf("Details", "Artworks", "Similar")
        icons = listOf(Icons.Outlined.Info, Icons.Outlined.AccountBox, ImageVector.vectorResource(id = R.drawable.baseline_person_search_24))
    }
    Scaffold(topBar = {NavBar2(navController = navController, artistName = artistName, artistDeets = artistDeets,
        authModel = authModel,
        showSnackbarmsg = { msg ->
            scope.launch { snackbarHostState.showSnackbar(msg) }
        })},
        snackbarHost = { SnackbarHost(snackbarHostState) }){innerPadding->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){

            TabRow(selectedTabIndex = currTab) {
                tabs.forEachIndexed { index, title->
                    Tab(
                        text = { Text(title) },
                        icon = {
                            when (val icon = icons[index]) {
                                is ImageVector -> Icon(imageVector = icon, contentDescription = null)
                                is Painter -> Icon(painter = icon, contentDescription = null)
                            }
                        },
                        selected = currTab == index,
                        onClick = { currTab = index }
                    )
                }
            }
            if(isLoading){
                Column (modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
                    CircularProgressIndicator()
                    Text(text = "Loading...")
                }
            }else {
                when (currTab) {
                    0 -> ShowDetails(artistDeets = artistDeets)
                    1 -> ShowArtworks(artworkDeets = artworkDeets)
                    2 -> ShowSimilar(
                        simArtDeets = simArtistDeets,
                        navController = navController,
                        authModel = authModel,
                        showSnackbarmsg = { msg ->
                            scope.launch { snackbarHostState.showSnackbar(msg) }
                        })
                }
            }
        }
    }
}

@Composable
fun ShowDetails(artistDeets: ArtistDetails?) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)){
        Text(text = artistDeets?.artist_name?: "",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.CenterHorizontally)
            )
        Text(text = "${artistDeets?.artist_nationality?: ""}, ${ artistDeets?.artist_bday ?: "" }-${artistDeets?.artist_dday?: ""}",
            modifier = Modifier
                .padding(bottom = 6.dp)
                .align(Alignment.CenterHorizontally))
        Text(text = artistDeets?.artist_biography?: "",
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(3.dp))
    }
}

@Composable
fun ShowArtworks(artworkDeets: List<ArtworkDetails>) {
    if(artworkDeets.isNotEmpty()){
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(artworkDeets){artwork->
                ArtworkCard(
                    imageUrl = artwork.artwork_img,
                    name = artwork.artwork_title,
                    artdate = artwork.artwork_date,
                    artworkId = artwork.artwork_id
                )
            }
        }
    }
    else{
        Box(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "No Artworks",
                modifier = Modifier.fillMaxWidth().background(color = topBarColor(), shape = RoundedCornerShape(12.dp))
                    .padding(12.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ArtworkCard(
    imageUrl: String,
    name: String,
    artdate: String,
    artworkId: String
) {
    val viewModel : categoriesmodel = viewModel()
    val categoriesDeets by viewModel.categoriesDeets.collectAsState()
    var isClicked by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading.collectAsState()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
        ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
            Text(text = "${name}, ${artdate}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold
            )
            Button(onClick = {
                isClicked= true
                viewModel.getCategories(artworkId)
            }, modifier = Modifier
                .padding(bottom = 7.dp)
                .align(Alignment.CenterHorizontally)) {
                Text(text = "View Categories")
            }
            if(isClicked){
                AlertDialog(
                    onDismissRequest = { isClicked = false },
                    title = { Text(text = "Categories") } ,
                    text = {
                        if(isLoading){
                            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator()
                                Text("Loading…", Modifier.padding(top = 8.dp))
                            }
                        }
                        else if(categoriesDeets.isNotEmpty()){
                            CarouselModal(categoriesDeets = categoriesDeets)
                        }else{
                            Text(text = "No Categories available")
                        }
                    },
                    confirmButton = { Button(onClick = { isClicked = false }) {
                        Text(text = "Close")
                    }}
                )
            }
        }
    }
}

@Composable
fun CarouselModal(categoriesDeets: List<Categories>) {
    var ind by remember { mutableStateOf(0) }
    var lastInd = categoriesDeets.size-1

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        IconButton(onClick = { if(ind>0) ind--
        else ind = lastInd }) {
            Icon(Icons.Default.KeyboardArrowLeft, "prev")
        }
        ModalCard(categoriesDeets[ind])
        IconButton(onClick = { if(ind<lastInd) ind++
        else ind = 0 }) {
            Icon(Icons.Default.KeyboardArrowRight, "next")
        }
    }
}

@Composable
fun ModalCard(modal: Categories) {
    Card(modifier = Modifier.fillMaxWidth(0.9f).height(420.dp), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = rememberAsyncImagePainter(modal.gene_img),
                contentDescription = "modal img",
                modifier = Modifier.width(225.dp).height(170.dp).clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = modal.gene_name, style = MaterialTheme.typography.titleMedium , fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            ArtsyLinkDesc(description = modal.gene_description, modifier = Modifier.padding(bottom = 8.dp, start = 4.dp, end = 4.dp).fillMaxWidth().verticalScroll(rememberScrollState()))
//            Text(text = modal.gene_description, modifier = Modifier.padding(bottom = 8.dp).verticalScroll(rememberScrollState()))
        }
    }
}

@Composable
fun ArtsyLinkDesc(description: String, modifier: Modifier = Modifier) {
    val annotatedString = buildAnnotatedString {
        val regex = Regex("""\[(.*?)]\((.*?)\)""")
        var lInd = 0
        regex.findAll(description).forEach { match ->
            val start = match.range.first
            val end = match.range.last + 1
            append(description.substring(lInd, start))

            val label = match.groupValues[1]
            val url = "https://www.artsy.net${match.groupValues[2]}"
            val sInd = length
            append(label)
            addStyle(
                style = SpanStyle(
                    color = Color(0xFF1149F6),
                    textDecoration = TextDecoration.Underline
                ),
                start = sInd,
                end = sInd + label.length
            )
            addStringAnnotation(
                tag = "URL",
                annotation = url,
                start = sInd,
                end = sInd + label.length
            )

            lInd = end
        }
        if (lInd < description.length) append(description.substring(lInd))

    }
    val context = LocalContext.current
    ClickableText(
        text = annotatedString,
        style = TextStyle(color = MaterialTheme.colorScheme.onBackground),
        modifier = modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    val intent = Intent(Intent.ACTION_VIEW, annotation.item.toUri())
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
        }
    )
}


@Composable
fun ShowSimilar(simArtDeets: List<ArtistSearchResults>, navController: NavHostController, authModel: UserAuth, showSnackbarmsg: (String) -> Unit) {
    if(simArtDeets.isNotEmpty()){
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(simArtDeets){artist->
                ArtistCard(
                    artist = artist,
                    authModel = authModel,
                    onClick = {
                        navController.navigate(Screen.artistDetails.createRoute(artist.artist_ID, artist.artist_Title))
                    },
                    showSnackbarmsg = showSnackbarmsg
                )
            }
        }
    }
    else{
        Box(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "No Similar Artists",
                modifier = Modifier.fillMaxWidth().background(color = topBarColor(), shape = RoundedCornerShape(12.dp))
                    .padding(12.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavBar2(navController: NavHostController, artistName: String, artistDeets: ArtistDetails?, authModel: UserAuth, showSnackbarmsg: (String) -> Unit){
    val isFav = authModel.favorites.value?.any { it.artistId == artistDeets?.artist_id } ==true
    TopAppBar(
        title = { Text(text = artistName) },
        modifier = Modifier.fillMaxWidth(),
        navigationIcon = {
            IconButton(onClick = {navController.popBackStack()}) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            if(authModel.isLoggedIn.value) {
                IconButton(onClick = {
                    val artistdeets = ArtistSearchResults(
                        artist_ID = artistDeets?.artist_id ?: "",
                        artist_Title = artistName,
                        artist_Image = "",
                    )
                    authModel.toggleFavs(artistdeets)
                    if (authModel.favorites.value?.any { it.artistId == artistDeets?.artist_id } == true) {
                        showSnackbarmsg("Removed from favorites")
                    } else {
                        showSnackbarmsg("Added to favorites")
                    }
                }) {
                    if(isFav) {
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
        },
        colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
            containerColor = topBarColor()
        )
    )
}

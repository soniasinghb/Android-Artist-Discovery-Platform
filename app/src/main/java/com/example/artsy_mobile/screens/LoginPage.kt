package com.example.artsy_mobile.screens

import android.app.Application
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.example.artsy_mobile.Screen
import com.example.artsy_mobile.ui.theme.topBarColor
import com.example.artsy_mobile.viewmodels.UserAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(navController: NavHostController, viewModel: UserAuth){
//    val context = LocalContext.current
    val snackBarHostStat = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
//
//    val viewModel: UserAuth = viewModel(
//        factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application)
//    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailValidationErr by remember { mutableStateOf("") }
    var passwordValidationErr by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf("") }
    val isLoginLoading by viewModel.isLoginLoading.collectAsState()

    Scaffold(snackbarHost = { SnackbarHost(snackBarHostStat) },
        topBar = {
        TopAppBar(
            title = { Text(text = "Login") },
            navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
            },
            colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                containerColor = topBarColor()
            )
        )
    }) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            OutlinedTextField(value = email,
                onValueChange = {email=it
                                emailValidationErr =""
                                    },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(0.9f)
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused) {
                            emailValidationErr = if(email.isBlank()){
                                "Email cannot be empty"}
                            else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                                "Invalid email format"
                            }else{
                                ""
                            }
                        }
                    },
                isError = emailValidationErr.isNotEmpty(),
                supportingText = { if(emailValidationErr.isNotEmpty()) Text(text = emailValidationErr, color = Color.Red) })

            Spacer(modifier = Modifier.padding(4.dp))
            OutlinedTextField(value = password,
                onValueChange = {password=it
                                passwordValidationErr= ""
                                },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(0.9f)
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused) {
                            passwordValidationErr = if(password.isBlank()) {
                                "Password cannot be empty"
                            }else{
                                ""
                            }
                        }
                    },
                isError =  passwordValidationErr.isNotEmpty(),
                supportingText = { if(passwordValidationErr.isNotEmpty()) Text(text = passwordValidationErr, color = Color.Red) })
            Spacer(modifier = Modifier.padding(8.dp))

            Button(onClick = {
                if(emailValidationErr.isEmpty() && passwordValidationErr.isEmpty()){
                    viewModel.login(
                        email = email,
                        password = password,
                        onSuccess = {
                            coroutineScope.launch {
                                snackBarHostStat.showSnackbar("Logged in Successfully")
                            }
                            navController.navigate(Screen.HomeScreen.route)
                        },
                        onFailure = {
                            loginError = it
                        }
                    )
                }
                //https://medium.com/@ramadan123sayed/comprehensive-guide-to-textfields-in-jetpack-compose-f009c4868c54
                //*******write login logic -> new func
                //snackbar too
                //change modelview state to loggedin
            }, modifier = Modifier
                .fillMaxWidth(0.9f),
                enabled = !isLoginLoading
            ) {
                if(isLoginLoading)
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                else Text(text = "Login")
            }
            Spacer(modifier = Modifier.padding(8.dp))
            if(loginError.isNotEmpty()){
                Text(text = loginError, color = Color.Red)
            }
            GotoReg(registerClick ={navController.navigate(Screen.Register.route)})
        }
    }
}

@Composable
fun GotoReg(registerClick: () -> Unit) {

    val context = LocalContext.current
    val hyperlinkText = buildAnnotatedString {
        append("Don't have an account yet?")
        pushStringAnnotation(tag = "register", annotation = "register")
        withStyle(style = SpanStyle(color = Color.Blue)) {
            append(" Register")
        }
        pop()
    }
    ClickableText(
        text = hyperlinkText,
        style = TextStyle(color = MaterialTheme.colorScheme.onBackground),
        onClick = { offset ->
            hyperlinkText.getStringAnnotations(tag = "register", start = offset, end = offset)
                .firstOrNull()?.let {
                    registerClick()
                }
        }
    )
}
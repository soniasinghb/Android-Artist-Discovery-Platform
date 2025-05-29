package com.example.artsy_mobile.screens

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.artsy_mobile.Screen
import com.example.artsy_mobile.ui.theme.topBarColor
import com.example.artsy_mobile.viewmodels.UserAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Register(navController: NavHostController, viewModel: UserAuth){
    val snackBarHostStat = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

//    val context = LocalContext.current
//    val viewModel: UserAuth = viewModel(
//        factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application)
//    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullname by remember { mutableStateOf("") }
    val isRegisterLoading by viewModel.isRegLoading.collectAsState()

    var emailValidationErr by remember { mutableStateOf("") }
    var passwordValidationErr by remember { mutableStateOf("") }
    var fullnameValidationErr by remember { mutableStateOf("") }
    var registerError by remember { mutableStateOf("") }

    Scaffold( snackbarHost = { SnackbarHost(snackBarHostStat) },
        topBar = {
        TopAppBar(
            title = { Text(text = "Register") },
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
            OutlinedTextField(value = fullname,
                onValueChange = {fullname=it
                    fullnameValidationErr = ""},
                label = { Text("Enter full name") },
                modifier = Modifier.fillMaxWidth(0.9f)
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused) {
                            fullnameValidationErr = if(fullname.isBlank()) {
                                "Full name cannot be empty"
                            }else{
                                ""
                            }
                        }
                    },
                isError = fullnameValidationErr.isNotEmpty(),
                supportingText = { if(fullnameValidationErr.isNotEmpty()) Text(text = fullnameValidationErr, color = Color.Red) }
            )
            Spacer(modifier = Modifier.padding(4.dp))

            OutlinedTextField(value = email, onValueChange = {
                email = it
                emailValidationErr = ""},
            label = { Text("Enter email") },
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
                onValueChange =
                {password=it
                passwordValidationErr=""},
                label = { Text("Password") },
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
                visualTransformation = PasswordVisualTransformation(),
                isError =  passwordValidationErr.isNotEmpty(),
                supportingText = { if(passwordValidationErr.isNotEmpty()) Text(text = passwordValidationErr, color = Color.Red) }
            )
            Spacer(modifier = Modifier.padding(8.dp))

            Button(onClick = {
                if(emailValidationErr.isEmpty() && passwordValidationErr.isEmpty() && fullnameValidationErr.isEmpty()){
                    viewModel.register(
                        fullname = fullname,
                        email = email,
                        password = password,
                        onSuccess = {
                            coroutineScope.launch {
                                snackBarHostStat.showSnackbar("Registered Successfully")
                            }
                            navController.navigate(Screen.HomeScreen.route)
                        },
                        onFailure = {
                            registerError = it
                        }
                    )
                }
            }, modifier = Modifier
                .fillMaxWidth(0.9f),
                enabled = !isRegisterLoading
            ) {
                if(isRegisterLoading)
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                else Text(text = "Register")
            }
            if(registerError.isNotEmpty()){
                Text(text = registerError, color = Color.Red)
            }
            Spacer(modifier = Modifier.padding(8.dp))
            GotoLogin(loginClick ={navController.navigate(Screen.Login.route)})
        }
    }
}

@Composable
fun GotoLogin(loginClick: () -> Unit) {

    val context = LocalContext.current
    val hyperlinkText = buildAnnotatedString {
        append("Already have an account?")
        pushStringAnnotation(tag = "Login", annotation = "Login")
        withStyle(style = SpanStyle(color = Color.Blue)) {
            append(" Login")
        }
        pop()
    }
    ClickableText(
        text = hyperlinkText,
        style = TextStyle(color = MaterialTheme.colorScheme.onBackground),
        onClick = { offset ->
            hyperlinkText.getStringAnnotations(tag = "Login", start = offset, end = offset)
                .firstOrNull()?.let {
                    loginClick()
                }
        }
    )
}
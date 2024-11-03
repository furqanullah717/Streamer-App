package com.codewithfk.streamer.ui.feature.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.codewithfk.streamer.navigation.NavRoute
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = hiltViewModel()) {

    LaunchedEffect(Unit) {
        viewModel.navigationState.collectLatest {
            when (it) {
                LoginNavigationEvent.NavigateToHome -> {
                    navController.navigate(NavRoute.Home.route) {
                        popUpTo(NavRoute.SignIn.route) { inclusive = true }
                    }
                }

                LoginNavigationEvent.NavigateToSignUp -> {
                    navController.navigate(NavRoute.SignUp.route)
                }
            }
        }
    }

    val uiState = viewModel.uiState.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Sign In", modifier = Modifier.padding(16.dp))
        when (uiState.value) {
            LoginEvent.Normal -> {

                val email = viewModel.email.collectAsStateWithLifecycle()
                val password = viewModel.password.collectAsStateWithLifecycle()
                val isEnabled = viewModel.buttonEnabled.collectAsStateWithLifecycle()
                OutlinedTextField(
                    value = email.value,
                    onValueChange = viewModel::onEmailChange,
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = password.value,
                    onValueChange = viewModel::onPasswordChange,
                    label = { Text("Password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Button(
                    onClick = { viewModel.onLoginClick() }, modifier = Modifier.fillMaxWidth(),
                    enabled = isEnabled.value
                ) {
                    Text(text = "Sign In")
                }
                TextButton(onClick = { viewModel.onSignUpClick() }) {
                    Text(text = "Don't have an account? SignUp")
                }
            }

            LoginEvent.Loading -> {
                CircularProgressIndicator()
                Text(text = "Loading...")
            }

            is LoginEvent.Error -> {
                Text(text = (uiState.value as LoginEvent.Error).message)
            }

            is LoginEvent.Success -> {
                Text(text = "Success")
            }
        }
    }

}
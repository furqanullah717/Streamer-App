package com.codewithfk.streamer.ui.feature.signup

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
fun SignUpScreen(navController: NavController, viewModel: SingUpViewModel = hiltViewModel()) {

    LaunchedEffect(Unit) {
        viewModel.navigationState.collectLatest {
            when (it) {
                SignUpNavigationEvent.NavigateToHome -> {
                    navController.navigate(NavRoute.Home.route) {
                        popUpTo(NavRoute.SignUp.route) { inclusive = true }
                    }
                }
                SignUpNavigationEvent.NavigateToLogin -> {
                    navController.popBackStack()
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
        Text(text = "Sign Up", modifier = Modifier.padding(16.dp))
        when (uiState.value) {
            SignUpEvent.Normal -> {

                val name = viewModel.name.collectAsStateWithLifecycle()
                val email = viewModel.email.collectAsStateWithLifecycle()
                val password = viewModel.password.collectAsStateWithLifecycle()
                val confirmPassword = viewModel.confirmPassword.collectAsStateWithLifecycle()
                val isEnabled = viewModel.buttonEnabled.collectAsStateWithLifecycle()
                OutlinedTextField(
                    value = name.value,
                    onValueChange = viewModel::onNameChange,
                    label = { Text("Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

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

                OutlinedTextField(
                    value = confirmPassword.value,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    label = { Text("Confirm Password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Button(
                    onClick = { viewModel.signUp() }, modifier = Modifier.fillMaxWidth(),
                    enabled = isEnabled.value
                ) {
                    Text(text = "Sign Up")
                }
                TextButton(onClick = { viewModel.onLoginButtonClicked() }) {
                    Text(text = "Already have an account? Login")
                }
            }

            SignUpEvent.Loading -> {
                CircularProgressIndicator()
                Text(text = "Loading...")
            }

            is SignUpEvent.Error -> {
                Text(text = (uiState.value as SignUpEvent.Error).message)
            }

            is SignUpEvent.Success -> {
                Text(text = "Success")
            }
        }
    }

}
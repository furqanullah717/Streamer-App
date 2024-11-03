package com.codewithfk.streamer.ui.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithfk.streamer.ui.feature.signup.SignUpNavigationEvent
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {
    private val _navigateState = MutableSharedFlow<LoginNavigationEvent>()
    val navigationState = _navigateState.asSharedFlow()

    private val uiEvent = MutableStateFlow<LoginEvent>(LoginEvent.Normal)
    val uiState = uiEvent.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _buttonEnabled = MutableStateFlow(false)
    val buttonEnabled = _buttonEnabled.asStateFlow()


    fun onEmailChange(email: String) {
        _email.value = email
        validate()
    }

    fun onPasswordChange(password: String) {
        _password.value = password
        validate()
    }

    private fun validate() {
        _buttonEnabled.value = _email.value.isNotEmpty() &&
                _password.value.isNotEmpty()
    }

    fun onLoginClick() {
        uiEvent.value = LoginEvent.Loading
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(_email.value, _password.value)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    uiEvent.value = LoginEvent.Success
                    viewModelScope.launch {
                        _navigateState.emit(LoginNavigationEvent.NavigateToHome)
                    }
                } else {
                    uiEvent.value = LoginEvent.Error(it.exception?.message ?: "An error occurred")
                }
            }
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            _navigateState.emit(LoginNavigationEvent.NavigateToSignUp)
        }
    }
}

sealed class LoginEvent {
    object Normal : LoginEvent()
    object Loading : LoginEvent()
    object Success : LoginEvent()
    data class Error(val message: String) : LoginEvent()
}

sealed class LoginNavigationEvent {
    object NavigateToHome : LoginNavigationEvent()
    object NavigateToSignUp : LoginNavigationEvent()
}
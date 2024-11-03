package com.codewithfk.streamer.ui.feature.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingUpViewModel @Inject constructor() : ViewModel() {
    private val _navigateState = MutableSharedFlow<SignUpNavigationEvent>()
    val navigationState = _navigateState.asSharedFlow()

    private val uiEvent = MutableStateFlow<SignUpEvent>(SignUpEvent.Normal)
    val uiState = uiEvent.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

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

    fun onConfirmPasswordChange(confirmPassword: String) {
        _confirmPassword.value = confirmPassword
        validate()
    }

    fun onNameChange(name: String) {
        _name.value = name
        validate()
    }


    private fun validate() {
        _buttonEnabled.value = _email.value.isNotEmpty() &&
                _password.value.isNotEmpty() &&
                _confirmPassword.value.isNotEmpty() &&
                _name.value.isNotEmpty() &&
                _password.value == _confirmPassword.value
    }

    fun signUp() {
        uiEvent.value = SignUpEvent.Loading
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(_email.value, _password.value)
            .addOnCompleteListener { result ->
                auth.currentUser?.updateProfile(
                    com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(_name.value)
                        .build()
                )
                if (result.isSuccessful) {
                    uiEvent.value = SignUpEvent.Success
                    viewModelScope.launch {
                        _navigateState.emit(SignUpNavigationEvent.NavigateToHome)
                    }
                } else {
                    uiEvent.value =
                        SignUpEvent.Error(result.exception?.message ?: "An error occurred")
                }
            }
    }

    fun onLoginButtonClicked() {
        viewModelScope.launch {
            _navigateState.emit(SignUpNavigationEvent.NavigateToLogin)
        }
    }
}

sealed class SignUpNavigationEvent {
    object NavigateToHome : SignUpNavigationEvent()
    object NavigateToLogin : SignUpNavigationEvent()
}

sealed class SignUpEvent {
    object Normal : SignUpEvent()
    object Loading : SignUpEvent()
    data class Error(val message: String) : SignUpEvent()
    object Success : SignUpEvent()
}
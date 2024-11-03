package com.codewithfk.streamer.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithfk.streamer.data.StreamHandler
import com.codewithfk.streamer.data.model.StreamData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val handler: StreamHandler) : ViewModel() {
    private val _navigateState = MutableSharedFlow<HomeNavigationEvent>()
    val navigationState = _navigateState.asSharedFlow()

    private val uiEvent = MutableStateFlow<HomeEvent>(HomeEvent.Normal)
    val uiState = uiEvent.asStateFlow()


    val stream = handler.streamDataFlow

    fun startStream() {
        val currentUser = Firebase.auth.currentUser
        viewModelScope.launch {
            uiEvent.value = HomeEvent.Loading
        }
        currentUser?.let {
            val streamData = StreamData(
                hostName = it.email ?: "",
                title = "${it.displayName}'s live",
                description = "This is a live stream by ${it.displayName}",
                liveID = "live_${it.uid + System.currentTimeMillis()}"
            )
            handler.startStream(streamData, onSuccess = {
                viewModelScope.launch {
                    uiEvent.value = HomeEvent.Normal
                }
                onStreamClicked(streamData)
            }, onError = {
                uiEvent.value = HomeEvent.Error(it)
            })
        }
    }

    fun onStreamClicked(item: StreamData) {
        viewModelScope.launch {
            _navigateState.emit(HomeNavigationEvent.OpenStream(item))
        }
    }
}

sealed class HomeNavigationEvent {
    data class OpenStream(val streamData: StreamData) : HomeNavigationEvent()
}

sealed class HomeEvent {
    object Normal : HomeEvent()
    object Loading : HomeEvent()
    data class Error(val message: String) : HomeEvent()
}
package com.codewithfk.streamer.ui.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.codewithfk.streamer.ui.feature.stream.StreamActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        viewModel.navigationState.collectLatest {
            when (it) {
                is HomeNavigationEvent.OpenStream -> {
                    val data = it.streamData
                    val isHost = Firebase.auth.currentUser?.email == data.hostName
                    navController.context.startActivity(
                        StreamActivity.newIntent(navController.context, data, isHost)
                    )
                }
            }
        }
    }
    val uiState = viewModel.uiState.collectAsState()
    val stream = viewModel.stream.collectAsState()

    when (uiState.value) {
        HomeEvent.Normal -> {
            Column(modifier = Modifier.fillMaxSize()) {
                Button(onClick = { viewModel.startStream() }) {
                    Text("Start Stream")
                }
                if (stream.value.isEmpty()) {
                    Column {
                        Text("No streams available")
                    }
                } else {
                    LazyColumn {
                        items(stream.value) { item ->

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .padding(16.dp)
                                    .background(color = Color.Gray.copy(alpha = 0.4f))
                                    .clickable {
                                        viewModel.onStreamClicked(item)
                                    }
                            ) {
                                Column {
                                    Text(
                                        text = item.title,
                                        modifier = Modifier.padding(8.dp),
                                        fontSize = 20.sp
                                    )
                                    Text(text = item.description)
                                }
                            }
                        }

                    }
                }
            }
        }

        is HomeEvent.Error -> {
            Column {
                Text("An error occurred: ${(uiState.value as HomeEvent.Error).message}")
                Button(onClick = { }) {
                    Text("OK")
                }
            }
        }

        HomeEvent.Loading -> {
            Column {
                CircularProgressIndicator()
                Text("Loading...")
            }
        }
    }

}
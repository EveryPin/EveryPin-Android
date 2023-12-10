package everypin.app.feature.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _authState = MutableStateFlow(AuthState.LOADING)
    val authState = _authState.asStateFlow()

    private val _shouldShowSplash = mutableStateOf(true)
    val shouldShowSplash by _shouldShowSplash

    init {
        checkAuthentication()
    }

    private fun checkAuthentication() {
        viewModelScope.launch {
            delay(1500L)
            _authState.value = AuthState.NOT_AUTHENTICATED
        }
    }

    fun hideSplashScreen(duration: Long = 500L) {
        viewModelScope.launch {
            delay(duration)
            _shouldShowSplash.value = false
        }
    }
}

enum class AuthState {
    LOADING, AUTHENTICATED, NOT_AUTHENTICATED
}
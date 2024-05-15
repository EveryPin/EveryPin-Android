package everypin.app.feature.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import everypin.app.data.repository.AuthRepository
import everypin.app.datastore.DataStorePreferences
import everypin.app.datastore.PreferencesKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences
) : ViewModel() {
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
            dataStorePreferences.getString(PreferencesKey.ACCESS_TOKEN).map { token ->
                if (token != null) {
                    AuthState.AUTHENTICATED
                } else {
                    AuthState.NOT_AUTHENTICATED
                }
            }.collectLatest {
                _authState.emit(it)
            }
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
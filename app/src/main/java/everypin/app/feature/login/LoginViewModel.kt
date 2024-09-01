package everypin.app.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import everypin.app.core.constant.ProviderType
import everypin.app.core.ui.state.SignInState
import everypin.app.data.repository.AuthRepository
import everypin.app.datastore.DataStorePreferences
import everypin.app.datastore.PreferencesKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStorePreferences: DataStorePreferences
) : ViewModel() {
    private val _authStatusEvent = MutableSharedFlow<AuthStatusEvent>()
    val authStatusEvent = _authStatusEvent.asSharedFlow()

    private var _shouldShowSplash = true
    val shouldShowSplash get() = _shouldShowSplash

    private val _signInState = MutableStateFlow<SignInState>(SignInState.Init)
    val signInState = _signInState.asStateFlow()

    init {
        checkAuthentication()
    }

    private fun checkAuthentication() {
        viewModelScope.launch {
            dataStorePreferences.getString(PreferencesKey.ACCESS_TOKEN).map { token ->
                if (token != null) {
                    AuthStatusEvent.AUTHENTICATED
                } else {
                    AuthStatusEvent.NOT_AUTHENTICATED
                }
            }.collectLatest {
                _authStatusEvent.emit(it)
                _shouldShowSplash = false
            }
        }
    }

    fun signIn(
        providerType: ProviderType,
        token: String
    ) {
        viewModelScope.launch {
            _signInState.emit(SignInState.Loading)

            authRepository.login(providerType, token).catch {
                _signInState.emit(SignInState.Error("로그인 진행 중 문제가 발생하였습니다.", it))
            }.collectLatest {
                _signInState.emit(SignInState.Success)
            }
        }
    }
}

enum class AuthStatusEvent {
    AUTHENTICATED, NOT_AUTHENTICATED
}
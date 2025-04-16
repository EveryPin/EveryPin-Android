package everypin.app.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import everypin.app.core.constant.ProviderType
import everypin.app.core.ui.state.SignInState
import everypin.app.core.utils.Logger
import everypin.app.data.repository.AuthRepository
import everypin.app.datastore.DataStorePreferences
import everypin.app.datastore.PreferencesKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStorePreferences: DataStorePreferences,
    private val messaging: FirebaseMessaging
) : ViewModel() {
    private val _authStatusEvent = Channel<AuthStatusEvent>(Channel.BUFFERED)
    val authStatusEvent = _authStatusEvent.receiveAsFlow()

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
                _authStatusEvent.send(it)
                _shouldShowSplash = false
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun signIn(
        providerType: ProviderType,
        token: String
    ) {
        viewModelScope.launch {
            _signInState.emit(SignInState.Loading)

            getFcmTokenFlow().flatMapConcat { fcmToken ->
                authRepository.login(providerType, token, fcmToken)
            }.catch {
                _signInState.emit(SignInState.Error("로그인 진행 중 문제가 발생하였습니다.", it))
            }.collectLatest {
                _signInState.emit(SignInState.Success)
            }
        }
    }

    private fun getFcmTokenFlow(): Flow<String> = callbackFlow {
        messaging.token.addOnCompleteListener { task ->
            when {
                task.isSuccessful -> {
                    val token = task.result
                    trySend(token)
                }

                task.exception != null -> {
                    Logger.e("FCM 토큰을 받아오는 중 에러 발생", task.exception)
                    trySend("")
                }

                else -> {
                    Logger.w("FCM 토큰을 받아올 수 없음.")
                    trySend("")
                }
            }

            close()
        }

        awaitClose()
    }
}

enum class AuthStatusEvent {
    AUTHENTICATED, NOT_AUTHENTICATED
}
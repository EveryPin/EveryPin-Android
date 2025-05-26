package everypin.app.feature.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import everypin.app.core.constant.ProviderType
import everypin.app.data.repository.AuthRepository
import everypin.app.feature.login.state.LoginEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val messaging: FirebaseMessaging
) : ViewModel() {
    private val _eventChannel = Channel<LoginEvent>(Channel.BUFFERED)
    val receiveEvent = _eventChannel.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun signIn(
        providerType: ProviderType,
        token: String
    ) {
        viewModelScope.launch {
            _eventChannel.send(LoginEvent.Loading)

            getFcmTokenFlow().flatMapConcat { fcmToken ->
                authRepository.login(providerType, token, fcmToken)
            }.catch {
                _eventChannel.send(LoginEvent.LoginFailed("로그인 진행 중 문제가 발생하였습니다.", it))
            }.collectLatest {
                _eventChannel.send(LoginEvent.LoginSuccess)
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
                    logcat(LogPriority.ERROR) {
                        task.exception?.asLog() ?: "FCM 토큰을 받아오는 중 에러 발생"
                    }
                    trySend("")
                }

                else -> {
                    logcat(LogPriority.WARN) { "FCM 토큰을 받아올 수 없음." }
                    trySend("")
                }
            }

            close()
        }

        awaitClose()
    }
}
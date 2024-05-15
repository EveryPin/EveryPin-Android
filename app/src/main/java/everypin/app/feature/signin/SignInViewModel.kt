package everypin.app.feature.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import everypin.app.core.constant.ProviderType
import everypin.app.core.ui.state.SignInState
import everypin.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _signInState = MutableStateFlow<SignInState>(SignInState.Init)
    val signInState = _signInState.asStateFlow()

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
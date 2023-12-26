package everypin.app.feature.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import everypin.app.core.constant.ProviderType
import everypin.app.core.ui.state.SignInState
import everypin.app.core.utils.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(): ViewModel() {
    private val _signInState = MutableStateFlow<SignInState>(SignInState.Init)
    val signInState = _signInState.asStateFlow()

    fun signIn(
        socialSignIn: Flow<Result<String?>>,
        providerType: ProviderType
    ) {
        _signInState.value = SignInState.Loading

        viewModelScope.launch {
            socialSignIn.catch {
                Logger.w(it.message.toString())
                _signInState.value = SignInState.Error(it)
            }.collectLatest { result ->
                result.onSuccess { idToken ->
                    Logger.v("소셜로그인 토큰: $idToken")
                    _signInState.value = if (idToken != null) {
                        SignInState.Success
                    } else {
                        SignInState.Error(Throwable("사용자 정보를 불러올 수 없습니다."))
                    }
                }.onFailure {
                    Logger.w(it.message.toString())
                    _signInState.value = SignInState.Error(it)
                }
            }
        }
    }
}
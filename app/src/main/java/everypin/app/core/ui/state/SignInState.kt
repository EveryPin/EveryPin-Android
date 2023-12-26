package everypin.app.core.ui.state

sealed class SignInState {
    data object Init : SignInState()
    data object Loading : SignInState()
    data object Success : SignInState()
    data class Error(val throwable: Throwable? = null) : SignInState()
}
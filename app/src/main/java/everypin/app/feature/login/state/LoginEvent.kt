package everypin.app.feature.login.state

sealed class LoginEvent {
    data object Loading: LoginEvent()
    data object LoginSuccess: LoginEvent()
    data class LoginFailed(val message: String, val t: Throwable): LoginEvent()
}
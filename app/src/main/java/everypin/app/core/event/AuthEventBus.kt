package everypin.app.core.event

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class AuthEventBus @Inject constructor() {
    private val _event = Channel<AuthEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    suspend fun notifyAuthExpired() {
        _event.send(AuthEvent.AuthExpired)
    }

    suspend fun notifySignOut() {
        _event.send(AuthEvent.SignOut)
    }

    suspend fun notifyNotAuthenticated() {
        _event.send(AuthEvent.NotAuthenticated)
    }

    suspend fun notifyAuthenticated() {
        _event.send(AuthEvent.Authenticated)
    }
}

sealed class AuthEvent {
    data object AuthExpired : AuthEvent()
    data object SignOut : AuthEvent()
    data object NotAuthenticated : AuthEvent()
    data object Authenticated : AuthEvent()
}
package everypin.app.feature.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import everypin.app.core.event.AuthEventBus
import everypin.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authEventBus: AuthEventBus
) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            authRepository.logout().catch {
                logcat(LogPriority.WARN) { it.asLog() }
                emit(Unit)
            }.collectLatest {
                authEventBus.notifySignOut()
            }
        }
    }
}
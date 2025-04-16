package everypin.app.feature.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import everypin.app.core.utils.Logger
import everypin.app.data.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _settingEvent = Channel<SettingEvent>(Channel.BUFFERED)
    val settingEvent = _settingEvent.receiveAsFlow()

    fun logout() {
        viewModelScope.launch {
            authRepository.logout().catch {
                Logger.w("로그아웃 요청 중 에러 발생", it)
                emit(Unit)
            }.collectLatest {
                _settingEvent.send(SettingEvent.LogoutSuccess)
            }
        }
    }
}

sealed class SettingEvent {
    data object LogoutSuccess : SettingEvent()
}
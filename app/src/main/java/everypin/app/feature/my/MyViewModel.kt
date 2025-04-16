package everypin.app.feature.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import everypin.app.core.utils.Logger
import everypin.app.data.model.Profile
import everypin.app.data.repository.ProfileRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _myEvent = Channel<MyEvent>(Channel.BUFFERED)
    val myEvent = _myEvent.receiveAsFlow()

    private val _profileState = MutableStateFlow<Profile?>(null)
    val profileState = _profileState.asStateFlow()

    fun getProfileMe() {
        viewModelScope.launch {
            profileRepository.getProfileMe().catch {
                Logger.e(it.message.toString(), it)
                _myEvent.send(MyEvent.ProfileLoadError(it))
            }.collectLatest {
                Logger.d("프로필 조회 성공")
                _profileState.value = it
            }
        }
    }
}

sealed class MyEvent {
    data class ProfileLoadError(val throwable: Throwable) : MyEvent()
}
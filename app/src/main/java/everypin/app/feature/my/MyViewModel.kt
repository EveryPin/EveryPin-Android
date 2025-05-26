package everypin.app.feature.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val _event = Channel<MyEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    private val _profileState = MutableStateFlow<MyUiState>(MyUiState.Loading)
    val profileState = _profileState.asStateFlow()

    init {
        fetchProfile()
    }

    fun fetchProfile() {
        viewModelScope.launch {
            profileRepository.getProfileMe().catch {
                _profileState.value = MyUiState.Error(it)
            }.collectLatest {
                _profileState.value = MyUiState.Success(it)
            }
        }
    }
}

sealed class MyUiState {
    data object Loading : MyUiState()
    data class Success(val profile: Profile) : MyUiState()
    data class Error(val throwable: Throwable) : MyUiState()
}

sealed class MyEvent {
    data class ProfileLoadError(val throwable: Throwable) : MyEvent()
}
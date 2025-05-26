package everypin.app.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import everypin.app.data.model.Profile
import everypin.app.data.repository.ProfileRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
class ProfileEditViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _event = Channel<ProfileEditEvent>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    private val _profileState = MutableStateFlow<ProfileEditUiState>(ProfileEditUiState.Loading)
    val profileState = _profileState.asStateFlow()

    init {
        fetchProfile()
    }

    fun fetchProfile() {
        viewModelScope.launch {
            profileRepository.getProfileMe().catch {
                _profileState.value = ProfileEditUiState.Error(it)
            }.collectLatest {
                _profileState.value = ProfileEditUiState.Success(it)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun updateProfile(
        displayId: String,
        name: String,
        selfIntroduction: String,
        imagePath: String? = null,
    ) {
        val uiSuccessState = _profileState.value as? ProfileEditUiState.Success
        uiSuccessState?.let {
            _profileState.value = it.copy(profileUpdateLoading = true)
        }

        viewModelScope.launch {
            profileRepository.updateProfile(
                displayId = displayId,
                name = name,
                selfIntroduction = selfIntroduction,
                imagePath = imagePath
            ).flatMapConcat {
                profileRepository.getProfileMe()
            }.catch { e ->
                logcat(LogPriority.ERROR) { e.asLog() }
                uiSuccessState?.let {
                    _profileState.value = it.copy(profileUpdateLoading = false)
                }
                _event.send(ProfileEditEvent.ProfileUpdateFailed(e))
            }.collectLatest {
                _profileState.value = ProfileEditUiState.Success(it)
                _event.send(ProfileEditEvent.ProfileUpdateSuccess)
            }
        }
    }
}

sealed class ProfileEditUiState {
    data object Loading : ProfileEditUiState()
    data class Success(val profile: Profile, val profileUpdateLoading: Boolean = false) :
        ProfileEditUiState()

    data class Error(val throwable: Throwable) : ProfileEditUiState()
}

sealed class ProfileEditEvent {
    data class ProfileUpdateFailed(val throwable: Throwable) : ProfileEditEvent()
    data object ProfileUpdateSuccess : ProfileEditEvent()
}
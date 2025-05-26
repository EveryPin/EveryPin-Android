package everypin.app.feature.addpin

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import everypin.app.data.repository.PostRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import logcat.asLog
import logcat.logcat
import javax.inject.Inject

@HiltViewModel
class AddPinViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {
    private val _selectedImageListState = MutableStateFlow<List<Uri>>(emptyList())
    val selectedImageListState get() = _selectedImageListState.asStateFlow()

    private val _pinState = MutableStateFlow<PinInfo?>(null)
    val pinState get() = _pinState.asStateFlow()

    private val _regPinEvent = Channel<RegPinEvent>(Channel.BUFFERED)
    val regPinEvent get() = _regPinEvent.receiveAsFlow()

    private var _isLoading = mutableStateOf(false)
    val isLoading get() = _isLoading.value

    fun addImage(imageUris: List<Uri>) {
        _selectedImageListState.update {
            val copyList = it.toMutableList()
            copyList.addAll(imageUris)
            copyList
        }
    }

    fun removeImage(index: Int) {
        _selectedImageListState.update {
            val copyList = it.toMutableList()
            copyList.removeAt(index)
            copyList
        }
    }

    fun regPin(content: String) {
        viewModelScope.launch {
            val pinState = pinState.value

            when {
                selectedImageListState.value.isEmpty() -> {
                    _regPinEvent.send(RegPinEvent.EmptyImage)
                }

                pinState == null -> {
                    _regPinEvent.send(RegPinEvent.EmptyAddress)
                }

                content.isEmpty() -> {
                    _regPinEvent.send(RegPinEvent.EmptyContent)
                }

                else -> {
                    _isLoading.value = true

                    postRepository.writePost(
                        content = content,
                        lat = pinState.lat,
                        lng = pinState.lng,
                        imagePaths = selectedImageListState.value.map { it.toFile().absolutePath }
                    ).catch {
                        logcat { it.asLog() }
                        _isLoading.value = false
                        _regPinEvent.send(RegPinEvent.Error(it))
                    }.collectLatest {
                        _isLoading.value = false
                        _regPinEvent.send(RegPinEvent.Success)
                    }
                }
            }
        }
    }

    fun setPinState(address: String, lat: Double, lng: Double) {
        _pinState.value = PinInfo(
            address = address,
            lat = lat,
            lng = lng
        )
    }
}

data class PinInfo(
    val address: String,
    val lat: Double,
    val lng: Double
)

sealed class RegPinEvent {
    data object Success : RegPinEvent()
    data class Error(val error: Throwable? = null) : RegPinEvent()
    data object EmptyImage : RegPinEvent()
    data object EmptyAddress : RegPinEvent()
    data object EmptyContent : RegPinEvent()
}
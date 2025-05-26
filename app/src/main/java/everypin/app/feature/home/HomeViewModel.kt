package everypin.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import everypin.app.data.model.PostPin
import everypin.app.data.repository.MapRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import logcat.asLog
import logcat.logcat
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mapRepository: MapRepository
) : ViewModel() {
    private val _pinsState = MutableStateFlow<List<PostPin>>(emptyList())
    val pinsState = _pinsState.asStateFlow()

    fun fetchPins(x: Double, y: Double, range: Double) {
        viewModelScope.launch {
            mapRepository.getPinsByRange(lng = x, lat = y, range = range).catch {
                logcat { it.asLog() }
            }.collectLatest {
                _pinsState.emit(it)
            }
        }
    }
}
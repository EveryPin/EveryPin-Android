package everypin.app.feature.addpin

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AddPinViewModel @Inject constructor(): ViewModel() {
    private val _selectedImageListState = MutableStateFlow<List<Uri>>(emptyList())
    val selectedImageListState get() = _selectedImageListState.asStateFlow()

    fun addImage(vararg images: Uri) {
        _selectedImageListState.update {
            val copyList = it.toMutableList()
            copyList.addAll(images)
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
}
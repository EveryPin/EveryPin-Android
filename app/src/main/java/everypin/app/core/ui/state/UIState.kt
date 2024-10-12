package everypin.app.core.ui.state

sealed class UIState<out T : Any> {
    data object Loading : UIState<Nothing>()
    data class Success<out T : Any>(val data: T) : UIState<T>()
    data class Error(val error: Throwable? = null) : UIState<Nothing>()
}
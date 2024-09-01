package everypin.app.core.ui.navigation

object GlobalNavigation {
    private val handlerMap = mutableMapOf<String, GlobalNavigationHandler>()

    fun addHandler(key: String, handler: GlobalNavigationHandler) {
        handlerMap[key] = handler
    }

    fun removeHandler(key: String) {
        handlerMap.remove(key)
    }

    fun navigateToSignIn() {
        handlerMap.values.forEach {
            it.onNavigateToSignIn()
        }
    }
}
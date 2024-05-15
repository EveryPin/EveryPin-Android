package everypin.app.core.ui.navigation

object GlobalNavigation {
    private var handler: GlobalNavigationHandler? = null

    fun setHandler(handler: GlobalNavigationHandler) {
        this.handler = handler
    }

    fun removeHandler() {
        handler = null
    }

    fun navigateToSignIn() {
        handler?.onNavigateToSignIn()
    }
}
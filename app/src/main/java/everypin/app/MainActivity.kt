package everypin.app

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import everypin.app.core.event.AuthEventBus
import everypin.app.core.ui.EveryPinApp
import everypin.app.core.ui.rememberEveryPinAppState
import everypin.app.core.ui.theme.EveryPinTheme
import everypin.app.datastore.DataStorePreferences
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var dataStorePreferences: DataStorePreferences

    @Inject
    lateinit var authEventBus: AuthEventBus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appState = rememberEveryPinAppState(
                navController = rememberNavController(),
                dataStorePreferences = dataStorePreferences,
                authEventBus = authEventBus,
                coroutineScope = rememberCoroutineScope()
            )

            DisposableEffect(Unit) {
                val listener = object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        return !appState.shouldShowSplash
                    }
                }
                val content: View = findViewById(android.R.id.content)
                content.viewTreeObserver.addOnPreDrawListener(listener)

                onDispose {
                    content.viewTreeObserver.removeOnPreDrawListener(listener)
                }
            }

            EveryPinTheme {
                EveryPinApp(
                    appState = appState
                )
            }
        }
    }
}
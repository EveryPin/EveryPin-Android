package everypin.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import everypin.app.core.event.AuthEvent
import everypin.app.core.event.AuthEventBus
import everypin.app.core.ui.EveryPinApp
import everypin.app.core.ui.rememberEveryPinAppState
import everypin.app.core.ui.theme.EveryPinTheme
import everypin.app.core.utils.Logger
import everypin.app.feature.login.LoginActivity
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        fun startActivityWithClearAllTasks(context: Context) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            context.startActivity(intent)
        }
    }

    @Inject
    lateinit var authEventBus: AuthEventBus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authEventBus.event.collect { event ->
                    when (event) {
                        AuthEvent.AuthExpired -> {
                            Logger.d("토큰 만료 이벤트 발생")
                            LoginActivity.Companion.startActivityWithClearAllTasks(this@MainActivity)
                        }

                        AuthEvent.SignOut -> {
                            Logger.d("로그아웃 이벤트 발생")
                            LoginActivity.Companion.startActivityWithClearAllTasks(this@MainActivity)
                        }
                    }
                }
            }
        }

        setContent {
            val appState = rememberEveryPinAppState(
                navController = rememberNavController()
            )

            EveryPinTheme {
                EveryPinApp(
                    appState = appState
                )
            }
        }
    }
}
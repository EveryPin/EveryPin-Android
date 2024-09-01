package everypin.app.feature.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import everypin.app.core.ui.theme.EveryPinTheme
import everypin.app.feature.main.MainActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    companion object {
        fun startActivityWithClearAllTasks(context: Context) {
            val intent = Intent(context, LoginActivity::class.java).apply {
                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            context.startActivity(intent)
        }
    }

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (loginViewModel.shouldShowSplash) {
                        false
                    } else {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    }
                }
            }
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                loginViewModel.authStatusEvent.collectLatest { event ->
                    when(event) {
                        AuthStatusEvent.AUTHENTICATED -> {
                            MainActivity.startActivityWithClearAllTasks(applicationContext)
                        }
                        AuthStatusEvent.NOT_AUTHENTICATED -> {}
                    }
                }
            }
        }

        setContent {
            EveryPinTheme {
                LoginScreen(
                    loginViewModel = loginViewModel,
                    onNavigateToHome = {
                        MainActivity.startActivityWithClearAllTasks(applicationContext)
                    }
                )
            }
        }
    }
}
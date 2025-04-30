package everypin.app.feature.login.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import everypin.app.R
import everypin.app.core.constant.ProviderType
import everypin.app.core.helper.rememberSocialSignInHelper
import everypin.app.core.ui.component.button.GoogleSignInButton
import everypin.app.core.ui.component.button.KakaoSignInButton
import everypin.app.core.ui.theme.EveryPinTheme
import everypin.app.core.ui.theme.LoadingBackgroundColor
import everypin.app.core.utils.Logger
import everypin.app.feature.login.state.LoginEvent
import everypin.app.feature.login.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
internal fun LoginRoute(
    loginViewModel: LoginViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit
) {
    val socialSignInHelper = rememberSocialSignInHelper()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showSignInErrorDialog by remember { mutableStateOf(false) }
    var signInErrorMsg by remember { mutableStateOf("") }
    var showLoading by remember { mutableStateOf(false) }

    if (showSignInErrorDialog) {
        AlertDialog(
            onDismissRequest = {
                showSignInErrorDialog = false
            },
            confirmButton = {
                TextButton(onClick = { showSignInErrorDialog = false }) {
                    Text(
                        text = stringResource(id = R.string.ok)
                    )
                }
            },
            text = {
                Text(
                    text = signInErrorMsg
                )
            }
        )
    }

    LaunchedEffect(Unit) {
        loginViewModel.receiveEvent.collect { event ->
            when (event) {
                LoginEvent.Loading -> {
                    showLoading = true
                }

                is LoginEvent.LoginFailed -> {
                    Logger.e(event.message, event.t)
                    showLoading = false
                    signInErrorMsg = event.message
                    showSignInErrorDialog = true
                }

                LoginEvent.LoginSuccess -> {
                    showLoading = false
                    onNavigateToHome()
                }
            }
        }
    }

    LoginScreen(
        showLoading = showLoading,
        onClickSignIn = { providerType ->
            scope.launch {
                socialSignInHelper.signIn(providerType).catch { error ->
                    Logger.e("소셜로그인 실패", error)
                    signInErrorMsg = ContextCompat.getString(context, R.string.sign_in_error)
                    showSignInErrorDialog = true
                }.collectLatest { token ->
                    if (token != null) {
                        loginViewModel.signIn(providerType, token)
                    } else {
                        signInErrorMsg = ContextCompat.getString(context, R.string.sign_in_error)
                        showSignInErrorDialog = true
                    }
                }
            }
        }
    )
}

@Composable
private fun LoginScreen(
    showLoading: Boolean,
    onClickSignIn: (ProviderType) -> Unit,
) {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 150.dp),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            textAlign = TextAlign.Center
                        )
                    )
                }
                Column(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 40.dp),
                ) {
                    GoogleSignInButton(
                        onClick = {
                            onClickSignIn(ProviderType.GOOGLE)
                        }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    KakaoSignInButton(
                        onClick = {
                            onClickSignIn(ProviderType.KAKAO)
                        }
                    )
                }
            }
            if (showLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = LoadingBackgroundColor)
                        .pointerInput(Unit) {},
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    EveryPinTheme {
        LoginScreen(
            showLoading = false,
            onClickSignIn = {},
        )
    }
}
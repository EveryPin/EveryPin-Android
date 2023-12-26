package everypin.app.feature.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import everypin.app.core.constant.ProviderType
import everypin.app.core.helper.rememberSocialSignInHelper
import everypin.app.core.ui.state.SignInState
import everypin.app.core.ui.theme.LoadingBackgroundColor

@Composable
internal fun SignInScreen(
    signInViewModel: SignInViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit
) {
    val signInState by signInViewModel.signInState.collectAsStateWithLifecycle()
    val socialSignInHelper = rememberSocialSignInHelper()

    LaunchedEffect(signInState) {
        if (signInState == SignInState.Success) {
            onNavigateToHome()
        }
    }

    SignInContainer(
        signInState = signInState,
        onClickSignIn = {
            signInViewModel.signIn(
                socialSignIn = socialSignInHelper.signIn(it),
                providerType = it
            )
        }
    )
}

@Composable
private fun SignInContainer(
    signInState: SignInState,
    onClickSignIn: (ProviderType) -> Unit
) {
    Scaffold { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Button(
                    onClick = {
                        onClickSignIn(ProviderType.KAKAO)
                    }
                ) {
                    Text(text = "카카오 로그인")
                }
            }
            if (signInState == SignInState.Loading) {
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
private fun SignInScreenPreview() {
    MaterialTheme {
        SignInContainer(
            signInState = SignInState.Init,
            onClickSignIn = {}
        )
    }
}
package everypin.app.feature.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.compose.EveryPinTheme
import com.example.compose.LoadingBackgroundColor
import everypin.app.core.constant.ProviderType
import everypin.app.core.helper.rememberSocialSignInHelper
import everypin.app.core.ui.component.button.GoogleSignInButton
import everypin.app.core.ui.component.button.KakaoSignInButton
import everypin.app.core.ui.state.SignInState

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
        },
        onNavigateToHome = onNavigateToHome
    )
}

@Composable
private fun SignInContainer(
    signInState: SignInState,
    onClickSignIn: (ProviderType) -> Unit,
    onNavigateToHome: () -> Unit
) {
    Scaffold { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, end = 20.dp, bottom = 40.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Button(onClick = onNavigateToHome) {
                        Text(text = "홈으로 이동")
                    }
                }
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

@PreviewScreenSizes
@Composable
private fun SignInScreenPreview() {
    EveryPinTheme {
        SignInContainer(
            signInState = SignInState.Init,
            onClickSignIn = {},
            onNavigateToHome = {}
        )
    }
}
package everypin.app.feature.signin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
internal fun SignInRoute(
    onClickSignIn: () -> Unit
) {
    SignInScreen(
        onClickSignIn = onClickSignIn
    )
}

@Composable
private fun SignInScreen(
    onClickSignIn: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Button(onClick = onClickSignIn) {
                Text(text = "로그인")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignInScreenPreview() {
    MaterialTheme {
        SignInScreen(
            onClickSignIn = {}
        )
    }
}
package everypin.app.core.helper

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import everypin.app.BuildConfig
import everypin.app.core.constant.ProviderType
import everypin.app.core.extension.findActivity
import everypin.app.core.utils.Logger
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

class SocialSignInHelper(
    private val activity: Activity
) {

    fun signIn(providerType: ProviderType): Flow<Result<String?>> {
        return when (providerType) {
            ProviderType.GOOGLE -> googleSignIn()
            ProviderType.KAKAO -> kakaoSignIn()
        }
    }

    private fun kakaoSignIn() = callbackFlow {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Logger.e("카카오계정으로 로그인 실패", error)
                trySend(Result.failure(error))
            } else if (token != null) {
                Logger.i("카카오계정으로 로그인 성공 ${token.accessToken}")
                trySend(Result.success(token.idToken))
            } else {
                trySend(Result.failure(Throwable("토큰을 가져올 수 없음.")))
            }
            close()
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(activity)) {
            UserApiClient.instance.loginWithKakaoTalk(activity) { token, error ->
                error?.let {
                    Logger.e("카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        trySend(Result.failure(Throwable("카카오 로그인이 취소됨.")))
                        close()
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(activity, callback = callback)
                    return@loginWithKakaoTalk
                }

                if (token != null) {
                    Logger.i("카카오톡으로 로그인 성공 ${token.accessToken}")
                    trySend(Result.success(token.idToken))
                } else {
                    trySend(Result.failure(Throwable("토큰을 가져올 수 없음.")))
                }
                close()
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(activity, callback = callback)
        }

        awaitClose()
    }

    private fun googleSignIn() = flow<Result<String?>> {
        val credentialManager = CredentialManager.create(activity)
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(BuildConfig.GOOGLE_SIGN_IN_SERVER_CLIENT_ID)
            .build()
        val getCredRequest = GetCredentialRequest(listOf(googleIdOption))
        val credentialResp = credentialManager.getCredential(
            context = activity,
            request = getCredRequest
        )
        val credential = credentialResp.credential as? CustomCredential

        val result = credential?.let {
            if (it.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(it.data)
                    Result.success(googleIdTokenCredential.idToken)
                } catch (e: GoogleIdTokenParsingException) {
                    Logger.e("Received an invalid google id token response", e)
                    Result.failure(e)
                }
            } else {
                Result.failure(Throwable("Unexpected type of credential"))
            }
        } ?: Result.failure(Throwable("Unexpected type of credential"))

        emit(result)
    }
}

@Composable
fun rememberSocialSignInHelper(): SocialSignInHelper {
    val context = LocalContext.current
    return remember {
        SocialSignInHelper(context.findActivity())
    }
}
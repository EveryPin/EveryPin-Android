package everypin.app.data.repository

import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import everypin.app.core.constant.ProviderType
import everypin.app.core.extension.toHttpError
import everypin.app.core.utils.Logger
import everypin.app.data.model.LoginRequest
import everypin.app.datastore.DataStorePreferences
import everypin.app.datastore.PreferencesKey
import everypin.app.network.api.AuthApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val dataStorePreferences: DataStorePreferences,
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {
    override fun login(
        providerType: ProviderType,
        accessToken: String,
        fcmToken: String
    ): Flow<Unit> = flow {
        val reqBody = LoginRequest(
            platformCode = when(providerType) {
                ProviderType.GOOGLE -> "google"
                ProviderType.KAKAO -> "kakao"
            },
            accessToken = accessToken,
            fcmToken = fcmToken
        )
        val resp = authApi.login(reqBody)
        val data = resp.body()

        if (resp.isSuccessful && data != null) {
            dataStorePreferences.putString(PreferencesKey.ACCESS_TOKEN, data.accessToken)
            dataStorePreferences.putString(PreferencesKey.REFRESH_TOKEN, data.refreshToken)
            emit(Unit)
        } else {
            throw resp.toHttpError()
        }
    }.flowOn(Dispatchers.IO)

    override fun logout(): Flow<Unit> = flow {
        val resp = authApi.logout()
        if (!resp.isSuccessful) {
            val error = resp.toHttpError()
            Logger.w("로그아웃 요청 실패", error)
        }

        dataStorePreferences.remove(PreferencesKey.ACCESS_TOKEN)
        dataStorePreferences.remove(PreferencesKey.REFRESH_TOKEN)

        firebaseAuth.signOut()
        emit(Unit)
    }.flowOn(Dispatchers.IO)

    override fun createFirebaseUser(
        userId: String,
        name: String,
        profileImageUrl: String?
    ): Flow<FirebaseUser> = callbackFlow {
        firebaseAuth.signInWithCustomToken(userId).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val firebaseUser = firebaseAuth.currentUser
                if (firebaseUser != null) {
                    val profileUpdates = UserProfileChangeRequest.Builder().apply {
                        displayName = name
                        photoUri = profileImageUrl?.toUri()
                    }.build()
                    firebaseUser.updateProfile(profileUpdates)
                    trySendBlocking(firebaseUser)
                } else {
                    close(NullPointerException("FirebaseAuth에서 받아온 사용자 정보가 null임."))
                }
            } else {
                close(task.exception)
            }
        }
        awaitClose()
    }.flowOn(Dispatchers.IO)
}
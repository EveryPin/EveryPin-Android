package everypin.app.data.repository

import everypin.app.core.constant.ProviderType
import everypin.app.core.extension.toHttpError
import everypin.app.datastore.DataStorePreferences
import everypin.app.datastore.PreferencesKey
import everypin.app.network.api.AuthenticationApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authenticationApi: AuthenticationApi,
    private val dataStorePreferences: DataStorePreferences
) : AuthRepository {
    override fun login(providerType: ProviderType, token: String): Flow<Unit> = flow {
        val resp = authenticationApi.login(
            platformCode = when (providerType) {
                ProviderType.GOOGLE -> "GOOGLE"
                ProviderType.KAKAO -> "KAKAO"
            },
            accessToken = token
        )
        val data = resp.body()

        if (resp.isSuccessful && data != null) {
            dataStorePreferences.putString(PreferencesKey.ACCESS_TOKEN, data.accessToken)
            dataStorePreferences.putString(PreferencesKey.REFRESH_TOKEN, data.refreshToken)
            emit(Unit)
        } else {
            throw resp.toHttpError()
        }
    }.flowOn(Dispatchers.IO)
}
package everypin.app.network.interceptor

import dagger.Lazy
import everypin.app.core.event.AuthEventBus
import everypin.app.datastore.DataStorePreferences
import everypin.app.datastore.PreferencesKey
import everypin.app.network.api.AuthApi
import everypin.app.network.model.auth.TokenRefreshRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val lazyDataStorePreferences: Lazy<DataStorePreferences>,
    private val lazyAuthApi: Lazy<AuthApi>,
    private val authEventBus: AuthEventBus
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val prefs = lazyDataStorePreferences.get()
        val tokenApi = lazyAuthApi.get()
        val request = chain.request()

        val hasAuthorization = request.headers.names().contains("Authorization")
        if (!hasAuthorization) return chain.proceed(request)

        val accessToken = runBlocking {
            prefs.getString(PreferencesKey.ACCESS_TOKEN).first()
        }
        val authenticatedRequest = request.newBuilder().apply {
            header("Authorization", "Bearer $accessToken")
        }
        val response = chain.proceed(authenticatedRequest.build())
        if (response.code == 401) {
            return runBlocking(Dispatchers.IO) {
                val refreshToken = prefs.getString(PreferencesKey.REFRESH_TOKEN).first()
                val requestBody = TokenRefreshRequest(accessToken, refreshToken)
                val tokenRefreshResponse = tokenApi.refresh(requestBody)
                val tokenData = tokenRefreshResponse.body()
                if (tokenRefreshResponse.isSuccessful && tokenData != null) {
                    prefs.putString(PreferencesKey.ACCESS_TOKEN, tokenData.accessToken)
                    prefs.putString(PreferencesKey.REFRESH_TOKEN, tokenData.refreshToken)
                    chain.proceed(
                        request.newBuilder().apply {
                            header("Authorization", "Bearer ${tokenData.accessToken}")
                        }.build()
                    )
                } else {
                    prefs.remove(PreferencesKey.ACCESS_TOKEN)
                    prefs.remove(PreferencesKey.REFRESH_TOKEN)
                    authEventBus.notifyAuthExpired()
                    response
                }
            }
        } else {
            return response
        }
    }
}
package everypin.app.network.di

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import everypin.app.core.event.AuthEventBus
import everypin.app.datastore.DataStorePreferences
import everypin.app.network.api.AuthApi
import everypin.app.network.interceptor.AuthInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InterceptorModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        lazyDataStorePreferences: Lazy<DataStorePreferences>,
        lazyAuthApi: Lazy<AuthApi>,
        authEventBus: AuthEventBus
    ): AuthInterceptor = AuthInterceptor(
        lazyDataStorePreferences = lazyDataStorePreferences,
        lazyAuthApi = lazyAuthApi,
        authEventBus = authEventBus
    )
}
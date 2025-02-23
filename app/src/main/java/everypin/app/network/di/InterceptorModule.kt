package everypin.app.network.di

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import everypin.app.datastore.DataStorePreferences
import everypin.app.network.api.AuthApi
import everypin.app.network.interceptor.AuthInterceptor

@Module
@InstallIn(SingletonComponent::class)
object InterceptorModule {

    @Provides
    fun provideAuthInterceptor(
        lazyDataStorePreferences: Lazy<DataStorePreferences>,
        lazyAuthApi: Lazy<AuthApi>
    ): AuthInterceptor = AuthInterceptor(lazyDataStorePreferences, lazyAuthApi)
}
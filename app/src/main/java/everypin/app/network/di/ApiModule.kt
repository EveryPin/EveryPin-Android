package everypin.app.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import everypin.app.network.api.AuthApi
import everypin.app.network.api.KakaoApi
import everypin.app.network.api.PostApi
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    fun provideAuthenticationApi(@RetrofitModule.AuthRetrofit retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    fun providePostApi(@RetrofitModule.AuthRetrofit retrofit: Retrofit): PostApi =
        retrofit.create(PostApi::class.java)

    @Provides
    fun provideKakaoApi(@RetrofitModule.KakaoRetrofit retrofit: Retrofit): KakaoApi =
        retrofit.create(KakaoApi::class.java)
}
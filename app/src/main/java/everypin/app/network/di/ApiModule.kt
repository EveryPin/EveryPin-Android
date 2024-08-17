package everypin.app.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import everypin.app.network.api.AuthenticationApi
import everypin.app.network.api.KakaoApi
import everypin.app.network.api.PostApi
import everypin.app.network.api.TokenApi
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    fun provideAuthenticationApi(@RetrofitModule.AuthRetrofit retrofit: Retrofit): AuthenticationApi =
        retrofit.create(AuthenticationApi::class.java)

    @Provides
    fun provideTokenApi(@RetrofitModule.DefaultRetrofit retrofit: Retrofit): TokenApi =
        retrofit.create(TokenApi::class.java)

    @Provides
    fun providePostApi(@RetrofitModule.AuthRetrofit retrofit: Retrofit): PostApi =
        retrofit.create(PostApi::class.java)

    @Provides
    fun provideKakaoApi(@RetrofitModule.KakaoRetrofit retrofit: Retrofit): KakaoApi =
        retrofit.create(KakaoApi::class.java)
}
package everypin.app.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import everypin.app.network.api.AuthApi
import everypin.app.network.api.KakaoApi
import everypin.app.network.api.MapApi
import everypin.app.network.api.PostApi
import everypin.app.network.api.ProfileApi
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideAuthenticationApi(@RetrofitModule.DefaultRetrofit retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun providePostApi(@RetrofitModule.AuthRetrofit retrofit: Retrofit): PostApi =
        retrofit.create(PostApi::class.java)

    @Provides
    @Singleton
    fun provideKakaoApi(@RetrofitModule.KakaoRetrofit retrofit: Retrofit): KakaoApi =
        retrofit.create(KakaoApi::class.java)

    @Provides
    @Singleton
    fun provideMapApi(@RetrofitModule.AuthRetrofit retrofit: Retrofit): MapApi =
        retrofit.create(MapApi::class.java)

    @Provides
    @Singleton
    fun provideProfileApi(@RetrofitModule.AuthRetrofit retrofit: Retrofit): ProfileApi =
        retrofit.create(ProfileApi::class.java)
}
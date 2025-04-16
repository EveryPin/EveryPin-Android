package everypin.app.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import everypin.app.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class DefaultRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class KakaoRetrofit

    @Provides
    @Singleton
    fun provideKotlinxSerializationConverterFactory() =
        Json.asConverterFactory("application/json; charset=UTF-8".toMediaType())

    @Provides
    @Singleton
    @DefaultRetrofit
    fun provideDefaultRetrofit(
        serializationConverterFactory: Converter.Factory,
        @OkHttpClientModule.DefaultOkHttpClient okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder().apply {
        baseUrl(BuildConfig.BASE_URL)
        addConverterFactory(serializationConverterFactory)
        client(okHttpClient)
    }.build()

    @Provides
    @Singleton
    @AuthRetrofit
    fun provideAuthRetrofit(
        serializationConverterFactory: Converter.Factory,
        @OkHttpClientModule.AuthOkHttpClient okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder().apply {
        baseUrl(BuildConfig.BASE_URL)
        addConverterFactory(serializationConverterFactory)
        client(okHttpClient)
    }.build()

    @Provides
    @Singleton
    @KakaoRetrofit
    fun provideKakaoRetrofit(
        serializationConverterFactory: Converter.Factory,
        @OkHttpClientModule.KakaoOkHttpClient okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder().apply {
        baseUrl("https://dapi.kakao.com")
        addConverterFactory(serializationConverterFactory)
        client(okHttpClient)
    }.build()
}
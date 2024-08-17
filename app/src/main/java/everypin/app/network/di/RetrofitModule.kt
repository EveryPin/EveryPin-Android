package everypin.app.network.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import everypin.app.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier

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
    fun provideMoshiConverterFactory(): MoshiConverterFactory =
        MoshiConverterFactory.create(Moshi.Builder().apply {
            addLast(KotlinJsonAdapterFactory())
        }.build())

    @Provides
    @DefaultRetrofit
    fun provideDefaultRetrofit(
        moshiConverterFactory: MoshiConverterFactory,
        @OkHttpClientModule.DefaultOkHttpClient okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder().apply {
        baseUrl(BuildConfig.BASE_URL)
        addConverterFactory(moshiConverterFactory)
        client(okHttpClient)
    }.build()

    @Provides
    @AuthRetrofit
    fun provideAuthRetrofit(
        moshiConverterFactory: MoshiConverterFactory,
        @OkHttpClientModule.AuthOkHttpClient okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder().apply {
        baseUrl(BuildConfig.BASE_URL)
        addConverterFactory(moshiConverterFactory)
        client(okHttpClient)
    }.build()

    @Provides
    @KakaoRetrofit
    fun provideKakaoRetrofit(
        moshiConverterFactory: MoshiConverterFactory,
        @OkHttpClientModule.KakaoOkHttpClient okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder().apply {
        baseUrl("https://dapi.kakao.com")
        addConverterFactory(moshiConverterFactory)
        client(okHttpClient)
    }.build()
}
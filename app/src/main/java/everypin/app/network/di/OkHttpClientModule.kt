package everypin.app.network.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import everypin.app.BuildConfig
import everypin.app.network.interceptor.AuthInterceptor
import everypin.app.network.interceptor.CacheInterceptor
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OkHttpClientModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class DefaultOkHttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthOkHttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class KakaoOkHttpClient

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideCacheInterceptor() = CacheInterceptor()

    @Provides
    @Singleton
    fun provideCache(
        @ApplicationContext context: Context
    ) = Cache(
        directory = File(context.cacheDir, "http_cache"),
        maxSize = 5L * 1024L * 1024L
    )

    @Provides
    @Singleton
    @DefaultOkHttpClient
    fun provideDefaultOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        cacheInterceptor: CacheInterceptor,
        cache: Cache
    ) = OkHttpClient.Builder().apply {
        connectTimeout(15, TimeUnit.SECONDS)
        readTimeout(15, TimeUnit.SECONDS)
        writeTimeout(15, TimeUnit.SECONDS)
        cache(cache)
        addInterceptor(cacheInterceptor)
        if (BuildConfig.DEBUG) {
            addInterceptor(loggingInterceptor)
        }
    }.build()

    @Provides
    @Singleton
    @AuthOkHttpClient
    fun provideAuthOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        cacheInterceptor: CacheInterceptor,
        cache: Cache
    ) = OkHttpClient.Builder().apply {
        connectTimeout(15, TimeUnit.SECONDS)
        readTimeout(15, TimeUnit.SECONDS)
        writeTimeout(15, TimeUnit.SECONDS)
        cache(cache)
        addInterceptor(authInterceptor)
        addInterceptor(cacheInterceptor)
        if (BuildConfig.DEBUG) {
            addInterceptor(loggingInterceptor)
        }
    }.build()

    @Provides
    @Singleton
    @KakaoOkHttpClient
    fun provideKakaoOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        cacheInterceptor: CacheInterceptor,
        cache: Cache
    ) = OkHttpClient.Builder().apply {
        connectTimeout(15, TimeUnit.SECONDS)
        readTimeout(15, TimeUnit.SECONDS)
        writeTimeout(15, TimeUnit.SECONDS)
        cache(cache)
        addInterceptor(cacheInterceptor)
        addInterceptor(Interceptor { chain ->
            val request = chain.request()
                .newBuilder().apply {
                    addHeader("Authorization", "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}")
                }.build()
            chain.proceed(request)
        })
        if (BuildConfig.DEBUG) {
            addInterceptor(loggingInterceptor)
        }
    }.build()
}
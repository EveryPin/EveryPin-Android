package everypin.app.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import everypin.app.data.repository.AuthRepository
import everypin.app.data.repository.AuthRepositoryImpl
import everypin.app.data.repository.KakaoRepository
import everypin.app.data.repository.KakaoRepositoryImpl
import everypin.app.data.repository.MapRepository
import everypin.app.data.repository.MapRepositoryImpl
import everypin.app.data.repository.PostRepository
import everypin.app.data.repository.PostRepositoryImpl
import everypin.app.data.repository.ProfileRepository
import everypin.app.data.repository.ProfileRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindPostRepository(postRepositoryImpl: PostRepositoryImpl): PostRepository

    @Binds
    abstract fun bindKakaoRepository(kakaoRepositoryImpl: KakaoRepositoryImpl): KakaoRepository

    @Binds
    abstract fun bindMapRepository(mapRepositoryImpl: MapRepositoryImpl): MapRepository

    @Binds
    abstract fun bindProfileRepository(profileRepositoryImpl: ProfileRepositoryImpl): ProfileRepository
}
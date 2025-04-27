package everypin.app.core.helper.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import everypin.app.core.helper.GeocoderHelper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HelperModule {

    @Provides
    @Singleton
    fun provideGeocoderHelper(
        @ApplicationContext context: Context
    ) = GeocoderHelper(context)
}
package com.gm.template.di

import android.app.Application
import coil.ImageLoader
import coil.memory.MemoryCache
import com.gm.template.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CoilModule {

    /**
     * Coil docs say: Coil performs best when you create a single ImageLoader and share it throughout your app. This is because each ImageLoader has its own memory cache, bitmap pool, and network observer.
     * For testing: https://coil-kt.github.io/coil/image_loaders/#testing
     */
    @Provides
    @Singleton
    fun provideImageLoader(app: Application): ImageLoader{
        return ImageLoader.Builder(app)
            .error(R.drawable.error_image)
            .placeholder(R.drawable.white_background)
            .memoryCache { MemoryCache.Builder(app).maxSizePercent(0.25).build() } // Don't know what is recommended?
            .crossfade(true)
            .build()
    }
}
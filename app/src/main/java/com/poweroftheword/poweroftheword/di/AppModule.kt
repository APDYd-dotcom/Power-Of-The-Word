package com.poweroftheword.poweroftheword.di

import android.content.Context
import androidx.room.Room
import com.poweroftheword.poweroftheword.data.local.AppDatabase
import com.poweroftheword.poweroftheword.data.local.AudioLikeDao
import com.poweroftheword.poweroftheword.data.local.FeedLikeDao
import com.poweroftheword.poweroftheword.data.local.VideoLikeDao
import com.poweroftheword.poweroftheword.data.local.VideoViewDao
import com.poweroftheword.poweroftheword.data.repository.ChurchRepositoryImpl
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(OkHttp) {
            install(HttpTimeout) {
                requestTimeoutMillis = 30000
                connectTimeoutMillis = 15000
                socketTimeoutMillis = 15000
            }
            install(HttpRequestRetry) {
                retryOnExceptionOrServerErrors(maxRetries = 3)
                exponentialDelay()
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                level = LogLevel.BODY
            }
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "power_of_the_word_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideVideoLikeDao(database: AppDatabase): VideoLikeDao {
        return database.videoLikeDao()
    }

    @Provides
    @Singleton
    fun provideAudioLikeDao(database: AppDatabase): AudioLikeDao {
        return database.audioLikeDao()
    }

    @Provides
    @Singleton
    fun provideFeedLikeDao(database: AppDatabase): FeedLikeDao {
        return database.feedLikeDao()
    }

    @Provides
    @Singleton
    fun provideVideoViewDao(database: AppDatabase): VideoViewDao {
        return database.videoViewDao()
    }

    @Provides
    @Singleton
    fun provideChurchRepository(
        client: HttpClient,
        @ApplicationContext context: Context,
        videoLikeDao: VideoLikeDao,
        audioLikeDao: AudioLikeDao,
        feedLikeDao: FeedLikeDao,
        videoViewDao: VideoViewDao
    ): ChurchRepository {
        return ChurchRepositoryImpl(
            client,
            context,
            videoLikeDao,
            audioLikeDao,
            feedLikeDao,
            videoViewDao
        )
    }
}

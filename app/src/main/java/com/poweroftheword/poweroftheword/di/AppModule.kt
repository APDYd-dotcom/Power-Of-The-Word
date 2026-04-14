package com.poweroftheword.poweroftheword.di

import android.content.Context
import androidx.room.Room
import com.poweroftheword.poweroftheword.data.local.AppDatabase
import com.poweroftheword.poweroftheword.data.local.VideoLikeDao
import com.poweroftheword.poweroftheword.data.repository.ChurchRepositoryImpl
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
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
        return HttpClient(CIO) {
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
        ).build()
    }

    @Provides
    @Singleton
    fun provideVideoLikeDao(database: AppDatabase): VideoLikeDao {
        return database.videoLikeDao()
    }

    @Provides
    @Singleton
    fun provideChurchRepository(
        client: HttpClient,
        @ApplicationContext context: Context,
        videoLikeDao: VideoLikeDao
    ): ChurchRepository {
        return ChurchRepositoryImpl(client, context, videoLikeDao)
    }
}

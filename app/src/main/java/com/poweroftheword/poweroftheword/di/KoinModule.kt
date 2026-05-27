package com.poweroftheword.poweroftheword.di

import androidx.room.Room
import com.poweroftheword.poweroftheword.data.local.AppDatabase
import com.poweroftheword.poweroftheword.data.repository.ChurchRepositoryImpl
import com.poweroftheword.poweroftheword.data.worker.AudioLikeSyncWorker
import com.poweroftheword.poweroftheword.data.worker.FeedLikeSyncWorker
import com.poweroftheword.poweroftheword.data.worker.LikeSyncWorker
import com.poweroftheword.poweroftheword.data.worker.VideoViewSyncWorker
import com.poweroftheword.poweroftheword.domain.repository.ChurchRepository
import com.poweroftheword.poweroftheword.service.FCMTopicManager
import com.poweroftheword.poweroftheword.ui.screens.about.PastorViewModel
import com.poweroftheword.poweroftheword.ui.screens.about.SocialMediaViewModel
import com.poweroftheword.poweroftheword.ui.screens.audio.AudioListViewModel
import com.poweroftheword.poweroftheword.ui.screens.dailyword.DailyWordViewModel
import com.poweroftheword.poweroftheword.ui.screens.feed.FeedViewModel
import com.poweroftheword.poweroftheword.ui.screens.home.HomeViewModel
import com.poweroftheword.poweroftheword.ui.screens.horaire.HoraireViewModel
import com.poweroftheword.poweroftheword.ui.screens.live.LiveViewModel
import com.poweroftheword.poweroftheword.ui.screens.program.ProgramViewModel
import com.poweroftheword.poweroftheword.ui.screens.radio.RadioViewModel
import com.poweroftheword.poweroftheword.ui.screens.settings.SettingsViewModel
import com.poweroftheword.poweroftheword.ui.screens.video.VideoListViewModel
import com.poweroftheword.poweroftheword.util.download.AudioDownloadManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val appModule = module {
    single {
        HttpClient(OkHttp) {
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

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "power_of_the_word_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().videoLikeDao() }
    single { get<AppDatabase>().audioLikeDao() }
    single { get<AppDatabase>().feedLikeDao() }
    single { get<AppDatabase>().videoViewDao() }

    single<ChurchRepository> {
        ChurchRepositoryImpl(
            client = get(),
            context = androidContext(),
            videoLikeDao = get(),
            audioLikeDao = get(),
            feedLikeDao = get(),
            videoViewDao = get()
        )
    }

    single { AudioDownloadManager(androidContext(), get()) }
    single { FCMTopicManager() }

    viewModel { HomeViewModel(get(), get(), get(), androidContext()) }
    viewModel { AudioListViewModel(get(), get(), get(), androidContext()) }
    viewModel { VideoListViewModel(get(), get(), get(), androidContext()) }
    viewModel { FeedViewModel(get(), get(), androidContext()) }
    viewModel { LiveViewModel(get(), androidContext()) }
    viewModel { DailyWordViewModel(get()) }
    viewModel { HoraireViewModel(get()) }
    viewModel { ProgramViewModel(get()) }
    viewModel { PastorViewModel(get()) }
    viewModel { SocialMediaViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { RadioViewModel(get(), get()) }

    worker { LikeSyncWorker(get(), get(), get(), get()) }
    worker { AudioLikeSyncWorker(get(), get(), get(), get()) }
    worker { FeedLikeSyncWorker(get(), get(), get(), get()) }
    worker { VideoViewSyncWorker(get(), get(), get(), get()) }
}

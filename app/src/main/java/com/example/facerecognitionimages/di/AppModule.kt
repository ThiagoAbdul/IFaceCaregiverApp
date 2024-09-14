package com.example.facerecognitionimages.di

import com.example.facerecognitionimages.data.services.CaregiverService
import com.example.facerecognitionimages.data.services.KnownPersonService
import com.example.facerecognitionimages.data.services.PwadService
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val appModule = module{

    singleOf(::CaregiverService)
    singleOf(::PwadService)
    singleOf(::KnownPersonService)

}

val networkModule = module {
    single {
        HttpClient(Android) {
            install(Logging) {
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }
}
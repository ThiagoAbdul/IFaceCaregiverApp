package com.example.facerecognitionimages.data.services

import com.example.facerecognitionimages.data.models.KnownPersonResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class KnownPersonService(private val http: HttpClient) {

    private val BASE_URL = "http://10.0.2.2:5132/api/KnownPerson"


    suspend fun getKnownPersonById(id: String) : KnownPersonResponse?{
        return http.get("${BASE_URL}/${id}").body()
    }
}
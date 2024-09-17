package com.example.facerecognitionimages.data.services

import com.example.facerecognitionimages.apiUrl
import com.example.facerecognitionimages.data.models.KnownPersonResponse
import com.example.facerecognitionimages.data.models.PwadResponse
import com.example.facerecognitionimages.data.models.RegisterPwadRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


class PwadService(private val http: HttpClient) {

    private val BASE_URL = apiUrl


    suspend fun addPwad(request: RegisterPwadRequest): PwadResponse{
        return http.post(BASE_URL + "/Pwad"){
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun listPwads(): List<PwadResponse>{
        return http.get(BASE_URL + "/Caregiver/PersonWithAlzheimerDisease").body()
    }

    suspend fun getPwadById(id: String): PwadResponse?{
        return http.get(BASE_URL + "/Pwad/" + id).body()
    }

    suspend fun listKnwonPersonByPwadId(pwadId: String): List<KnownPersonResponse>{
        return http.get("${BASE_URL}/Pwad/${pwadId}/KnownPersons").body()
    }

}

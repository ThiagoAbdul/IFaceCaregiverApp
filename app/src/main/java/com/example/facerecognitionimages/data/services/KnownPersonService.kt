package com.example.facerecognitionimages.data.services

import com.example.facerecognitionimages.apiUrl
import com.example.facerecognitionimages.data.models.AddImageRequest
import com.example.facerecognitionimages.data.models.KnownPersonResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class KnownPersonService(private val http: HttpClient) {

    private val BASE_URL = apiUrl+ "/KnownPerson"

    suspend fun getKnownPersonById(id: String) : KnownPersonResponse?{
        return http.get("${BASE_URL}/${id}").body()
    }

    suspend fun addFileForKnownPerson(knownPersonId: String, embedding: String) : Any{
        return http.submitForm(
            url = "${BASE_URL}/${knownPersonId}/AddImage",
            formParameters = parameters {
                append("Embedding", embedding)
            }
        )
    }

    suspend fun addImageForKnownPerson(knownPersonId: String, request: AddImageRequest) : Any{
        return http.post("${BASE_URL}/${knownPersonId}/AddImage"){
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
}
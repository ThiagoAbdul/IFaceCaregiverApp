package com.example.facerecognitionimages.data.services

import com.example.facerecognitionimages.data.models.PwadLocationResponse
import com.example.facerecognitionimages.locationApiUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class LocationService(private val http: HttpClient) {



    suspend fun getLastLocation(pwadId: String): PwadLocationResponse?{
        val url = locationApiUrl + "/user/"  + pwadId + "/last"

        return http.get(url).body()


    }
}
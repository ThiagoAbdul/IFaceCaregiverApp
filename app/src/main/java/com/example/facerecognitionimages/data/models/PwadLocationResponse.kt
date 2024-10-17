package com.example.facerecognitionimages.data.models

import kotlinx.serialization.Serializable

@Serializable
data class PwadLocationResponse(val userId: String,
                                val houseNumber: String,
                                val road: String,
                                val suburb: String,
                                val city: String,
                                val state: String,
                                val region: String,
                                val postcode: String,
                                val country: String, val date: String
    )
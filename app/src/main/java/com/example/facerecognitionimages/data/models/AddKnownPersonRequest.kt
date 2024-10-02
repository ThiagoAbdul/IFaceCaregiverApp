package com.example.facerecognitionimages.data.models

import kotlinx.serialization.Serializable

@Serializable
data class AddKnownPersonRequest(val firstName: String, val lastName: String, val description: String)
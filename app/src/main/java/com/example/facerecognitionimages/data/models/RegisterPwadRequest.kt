package com.example.facerecognitionimages.data.models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterPwadRequest(val firstName: String, val lastName: String)

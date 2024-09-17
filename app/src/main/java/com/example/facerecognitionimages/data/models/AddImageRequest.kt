package com.example.facerecognitionimages.data.models

import kotlinx.serialization.Serializable

@Serializable
data class AddImageRequest(val base64Image: String, val embedding: String)
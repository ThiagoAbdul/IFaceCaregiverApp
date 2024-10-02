package com.example.facerecognitionimages.data.models

import kotlinx.serialization.Serializable

@Serializable
class PwadResponse : java.io.Serializable{

    lateinit var id: String
    lateinit var person: Person
    lateinit var carefulToken: String
}
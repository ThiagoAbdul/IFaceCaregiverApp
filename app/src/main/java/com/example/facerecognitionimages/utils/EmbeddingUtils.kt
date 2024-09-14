package com.example.facerecognitionimages.utils

fun parseEmbeddingToString(embedding: Array<FloatArray>): String{
    val strBuilder = StringBuilder()
    for (f in embedding[0]) {
        strBuilder.append("$f,")
    }

    return strBuilder.toString()
}
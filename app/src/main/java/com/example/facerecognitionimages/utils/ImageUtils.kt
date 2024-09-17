package com.example.facerecognitionimages.utils

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Base64
import java.io.ByteArrayOutputStream

fun croppImage(bounds: Rect, inputImage: Bitmap, squareSize: Int) : Bitmap {
    if (bounds.top < 0) {
        bounds.top = 0
    }
    if (bounds.left < 0) {
        bounds.left = 0
    }
    if (bounds.right > inputImage.width) {
        bounds.right = inputImage.width - 1
    }
    if (bounds.bottom > inputImage.height) {
        bounds.bottom = inputImage.height - 1
    }
    //TODO crop the face
    var croppedFace = Bitmap.createBitmap(
        inputImage,
        bounds.left,
        bounds.top,
        bounds.width(),
        bounds.height()
    )
    croppedFace = Bitmap.createScaledBitmap(
        croppedFace,
        squareSize,
        squareSize,
        false
    )

    return croppedFace
}

fun bitmapToBase64(bitmap: Bitmap): String{
    val byteArrayOutputStream = ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream .toByteArray()

    return Base64.encodeToString(byteArray, Base64.DEFAULT)

}
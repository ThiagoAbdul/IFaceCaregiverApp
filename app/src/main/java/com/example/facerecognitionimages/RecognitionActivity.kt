package com.example.facerecognitionimages

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.facerecognitionimages.face_recognition.FaceClassifier
import com.example.facerecognitionimages.face_recognition.TFLiteFaceRecognition
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.IOException

class RecognitionActivity : AppCompatActivity() {
    var galleryCard: CardView? = null
    var cameraCard: CardView? = null
    var imageView: ImageView? = null
    var image_uri: Uri? = null

    //TODO declare face detector
    var detector: FaceDetector? = null

    //TODO declare face recognizer
    private var faceClassifier: FaceClassifier? = null

    //TODO get the image from gallery and display it
    var galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            image_uri = result.data!!.data
            val inputImage = uriToBitmap(image_uri!!)
            val rotated = rotateBitmap(inputImage)
            imageView!!.setImageBitmap(rotated)
            performFaceDetection(rotated)
        }
    }

    //TODO capture the image using camera and display it
    var cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result!!.resultCode == RESULT_OK) {
            val inputImage = uriToBitmap(image_uri!!)
            val rotated = rotateBitmap(inputImage)
            imageView!!.setImageBitmap(rotated)
            performFaceDetection(rotated)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recognition)

        //TODO handling permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_DENIED
            ) {
                val permission =
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, PERMISSION_CODE)
            }
        }

        //TODO initialize views
        galleryCard = findViewById(R.id.gallerycard)
        cameraCard = findViewById(R.id.cameracard)
        imageView = findViewById(R.id.imageView2)

        //TODO code for choosing images from gallery
        galleryCard!!.setOnClickListener(View.OnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryActivityResultLauncher.launch(galleryIntent)
        })

        //TODO code for capturing images using camera
        cameraCard!!.setOnClickListener(View.OnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    == PackageManager.PERMISSION_DENIED
                ) {
                    val permission = arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    requestPermissions(permission, PERMISSION_CODE)
                } else {
                    openCamera()
                }
            } else {
                openCamera()
            }
        })

        //TODO initialize face detector
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
        detector = FaceDetection.getClient(highAccuracyOpts)

        //TODO initialize face recognition model
        try {
            faceClassifier = TFLiteFaceRecognition.create(
                assets,
                "facenet.tflite",
                TF_OD_API_INPUT_SIZE,
                false, applicationContext
            )
            //cropSize = TF_OD_API_INPUT_SIZE;
        } catch (e: IOException) {
            e.printStackTrace()
            val toast = Toast.makeText(
                applicationContext,
                "Classifier could not be initialized",
                Toast.LENGTH_SHORT
            )
            toast.show()
            finish()
        }
    }

    //TODO opens camera so that user can capture image
    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        cameraActivityResultLauncher.launch(cameraIntent)
    }

    //TODO takes URI of the image and returns bitmap

    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    //TODO rotate image if image captured on samsung devices
    //TODO Most phone cameras are landscape, meaning if you take the photo in portrait, the resulting photos will be rotated 90 degrees.
    @SuppressLint("Range")
    fun rotateBitmap(input: Bitmap?): Bitmap {
        val orientationColumn =
            arrayOf(MediaStore.Images.Media.ORIENTATION)
        val cur =
            contentResolver.query(image_uri!!, orientationColumn, null, null, null)
        var orientation = -1
        if (cur != null && cur.moveToFirst()) {
            orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]))
        }
        Log.d("tryOrientation", orientation.toString() + "")
        val rotationMatrix = Matrix()
        rotationMatrix.setRotate(orientation.toFloat())
        return Bitmap.createBitmap(
            input!!,
            0,
            0,
            input.width,
            input.height,
            rotationMatrix,
            true
        )
    }

    //TODO perform face recognition
    fun performFaceDetection(inputImage: Bitmap) {
        val mutableBmp = inputImage.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBmp)
        val image = InputImage.fromBitmap(inputImage, 0)
        val result = detector!!.process(image)
            .addOnSuccessListener { faces ->
                for (face in faces) {
                    val bounds = face.boundingBox
                    val p = Paint()
                    p.style = Paint.Style.STROKE
                    p.strokeWidth = 3f
                    p.color = Color.RED
                    canvas.drawRect(bounds, p)
                    performFaceRecognition(bounds, inputImage, canvas)
                }
                imageView!!.setImageBitmap(mutableBmp)
            }
            .addOnFailureListener {
                // Task failed with an exception
                // ...
            }
    }

    fun performFaceRecognition(bounds: Rect, inputImage: Bitmap, canvas: Canvas) {
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
            TF_OD_API_INPUT_SIZE,
            TF_OD_API_INPUT_SIZE,
            false
        )

        //TODO fet the embedding for face
        val startTime = SystemClock.uptimeMillis()
        val result: FaceClassifier.Recognition? = faceClassifier!!.recognizeImage(croppedFace, true)
        val lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime
        if (result != null) {
            val conf: Float = result.distance!!
            if (conf < 1.0f) {
                val paint = Paint()
                if (result.id.equals("0")) {
                    paint.color = Color.GREEN
                } else {
                    paint.color = Color.RED
                }
                paint.textSize = 104f
                canvas.drawText(
                    result.title + "  " + conf,
                    bounds.left.toFloat(),
                    bounds.top.toFloat(),
                    paint
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        detector!!.close()
    }

    companion object {
        private const val TF_OD_API_INPUT_SIZE = 160
        const val PERMISSION_CODE = 100
    }
}
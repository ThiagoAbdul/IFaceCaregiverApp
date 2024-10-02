package com.example.facerecognitionimages

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.facerecognitionimages.data.models.AddImageRequest
import com.example.facerecognitionimages.data.services.KnownPersonService
import com.example.facerecognitionimages.face_recognition.FaceClassifier
import com.example.facerecognitionimages.face_recognition.FaceClassifier.Recognition
import com.example.facerecognitionimages.face_recognition.TFLiteFaceRecognition
import com.example.facerecognitionimages.utils.bitmapToBase64
import com.example.facerecognitionimages.utils.croppImage
import com.example.facerecognitionimages.utils.parseEmbeddingToString
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.IOException

class RegisterActivity : AppCompatActivity() {
    var galleryCard: CardView? = null
    var cameraCard: CardView? = null
    var imageView: ImageView? = null
    var image_uri: Uri? = null
    private var knownPersonId: String? = null
    private val knownPersonService: KnownPersonService by inject()

    //TODO declare face detector
    private var detector: FaceDetector? = null

    //TODO declare face recognizer
    private var faceClassifier: FaceClassifier? = null

    //TODO get the image from gallery and display it
    var galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result != null) {
            if (result.resultCode == RESULT_OK) {
                image_uri = result.data!!.data
                val inputImage = uriToBitmap(image_uri!!)
                val rotated = rotateBitmap(inputImage)
                imageView!!.setImageBitmap(rotated)
                performFaceDetection(rotated)
            }
        }
    }

    //TODO capture the image using camera and display it
    var cameraActivityResultLauncher = registerForActivityResult(ActivityResultContracts
        .StartActivityForResult()) { result ->
        if (result != null) {
            if (result.resultCode == RESULT_OK) {
                val inputImage = uriToBitmap(image_uri!!)
                val rotated = rotateBitmap(inputImage)
                imageView!!.setImageBitmap(rotated)
                performFaceDetection(rotated)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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

        knownPersonId = intent.extras?.getString("knownPerson")

        //TODO code for choosing images from gallery
        galleryCard!!.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryActivityResultLauncher.launch(galleryIntent)
        }

        //TODO code for capturing images using camera
        cameraCard!!.setOnClickListener {
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
            }

            openCamera()
        }

        //TODO initialize face detector
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
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

    //TODO perform face detection
    fun performFaceDetection(inputImage: Bitmap) {
        val mutableBmp = inputImage.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBmp)
        val image = InputImage.fromBitmap(inputImage, 0)
        detector!!.process(image)
            .addOnSuccessListener { faces ->
                for (face in faces) {
                    val bounds = face.boundingBox
                    val p = Paint()
                    p.style = Paint.Style.STROKE
                    p.strokeWidth = 3f
                    p.color = Color.RED
                    canvas.drawRect(bounds, p)
                    performFaceRecognition(bounds, inputImage)
                }
                imageView!!.setImageBitmap(mutableBmp)
            }
            .addOnFailureListener {
                // Task failed with an exception
                // ...
            }
    }

    //TODO perform face recognition
    fun performFaceRecognition(bounds: Rect, inputImage: Bitmap) {

        val croppedFace = croppImage(bounds, inputImage, TF_OD_API_INPUT_SIZE)

        //TODO fet the embedding for face
        val startTime = SystemClock.uptimeMillis()
        val result = faceClassifier!!.recognizeImage(croppedFace, true)
        val lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime
        result?.let { registerFaceDialogue(croppedFace, it) }
    }

    private fun registerFaceDialogue(croppedFace: Bitmap, rec: Recognition) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.register_face_dialogue)
        val ivFace = dialog.findViewById<ImageView>(R.id.dlg_image)

        val register = dialog.findViewById<Button>(R.id.button2)
        ivFace.setImageBitmap(croppedFace)
        register.setOnClickListener {


            val embedding = parseEmbeddingToString(rec.embeeding as Array<FloatArray>)

            if(knownPersonId != null){
                saveImage(embedding, croppedFace) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Face Registered Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()
                }
            }

//            faceClassifier!!.register(name, rec)


        }

        dialog.show()
    }

    private fun saveImage(embedding: String, bitmap: Bitmap, action: () -> Unit){
        if(knownPersonId == null)
            return
        CoroutineScope(Dispatchers.Main).launch {
            knownPersonService.addImageForKnownPerson(
                knownPersonId!!,
                AddImageRequest(base64Image = bitmapToBase64(bitmap), embedding)
            )
            action()
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
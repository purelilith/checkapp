package com.benefind.checkapp

import android.graphics.BitmapFactory
import android.media.Image
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File
import java.io.IOException
import android.net.Uri
import android.Manifest
import android.content.pm.PackageManager
import java.util.Locale
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import org.w3c.dom.Text
import java.util.Date
import com.google.mlkit.vision.text.TextRecognition
import java.text.SimpleDateFormat
import android.graphics.Bitmap
import android.text.method.ScrollingMovementMethod
import androidx.core.app.ActivityCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class OCRActivity: AppCompatActivity() {

    private lateinit var cameraImage: ImageView
    private lateinit var captureImgBtn: Button
    private lateinit var resultText: TextView

    private var currentPhotoPath: String? = null
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr)

        cameraImage = findViewById(R.id.cameraImage)
        captureImgBtn = findViewById(R.id.captureImgBtn)
        resultText = findViewById(R.id.resultText)

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
                isGranted ->
            if (isGranted){
                captureImage()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        takePictureLauncher= registerForActivityResult(ActivityResultContracts.TakePicture()){
            success ->
            if (success){
                currentPhotoPath?.let { path ->
                    val bitmap = BitmapFactory.decodeFile(path)
                    cameraImage.setImageBitmap(bitmap)
                    recognizeText(bitmap)
                }
            }
        }
        captureImgBtn.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("csdcsd", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }

    }

    private fun captureImage(){
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException){
            Toast.makeText(this, "Error occured while creating the file", Toast.LENGTH_SHORT).show()
            null
        }
        photoFile?.also {
            val photoUri: Uri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", it)
            takePictureLauncher.launch(photoUri)
        }
    }
    private fun recognizeText(bitmap: Bitmap){
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image).addOnSuccessListener { ocrText ->
            resultText.text = ocrText.text
            resultText.movementMethod = ScrollingMovementMethod.getInstance()


        }
    }
}
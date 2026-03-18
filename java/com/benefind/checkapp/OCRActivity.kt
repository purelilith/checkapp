package com.benefind.checkapp

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class OCRActivity : AppCompatActivity() {

    private lateinit var engCaptureImgBtn: Button
    var recognizedAdditives = ""
    private lateinit var cameraImage: ImageView
    private lateinit var captureImgBtn: Button
    private lateinit var resultText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var takeCharBtn: Button
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var currentPhotoPath: String? = null
    private lateinit var backButtonOCR: ImageView
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>

    // Инициализация распознавателя ML Kit для латиницы
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr)

        // Привязка UI элементов
        backButtonOCR = findViewById(R.id.backButtonOCR)
        progressBar = findViewById(R.id.progressBar)
        engCaptureImgBtn = findViewById(R.id.engCaptureImgBtn)
        cameraImage = findViewById(R.id.cameraImage)
        captureImgBtn = findViewById(R.id.captureImgBtn)
        resultText = findViewById(R.id.resultText)
        takeCharBtn = findViewById(R.id.takeCharBtn)

        progressBar.visibility = View.GONE

        // Разрешение на камеру
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) captureImage() else Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }

        // Лаунчер для фото с камеры
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                currentPhotoPath?.let { path ->
                    val bitmap = BitmapFactory.decodeFile(path)
                    processImage(bitmap)
                }
            }
        }

        // Лаунчер для выбора из галереи
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                processImage(bitmap)
            }
        }

        captureImgBtn.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        val galleryBtn: Button = findViewById(R.id.engCaptureImgBtn)
        galleryBtn.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        engCaptureImgBtn.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        backButtonOCR.setOnClickListener {
            finish()
        }

        takeCharBtn.setOnClickListener {
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("recognizedText", recognizedAdditives)
            startActivity(intent)
        }
    }// Основная функция распознавания через ML Kit
    private fun processImage(bitmap: Bitmap?) {
        if (bitmap == null) return

        cameraImage.setImageBitmap(bitmap)
        progressBar.visibility = View.VISIBLE
        resultText.isEnabled = false

        val image = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                progressBar.visibility = View.GONE
                resultText.isEnabled = true

                // Фильтруем текст: оставляем только латиницу, цифры и пробелы
                val rawText = visionText.text
                var cleanText = rawText.replace(Regex("[^a-zA-Z0-9 ]"), " ")
                cleanText = cleanText.replace(Regex("\\s+"), " ").trim()

                resultText.text = cleanText
                recognizedAdditives = cleanText
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                resultText.isEnabled = true
                Toast.makeText(this, "Recognition failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun captureImage() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            Toast.makeText(this, "Error occurred while creating the file", Toast.LENGTH_SHORT).show()
            null
        }
        photoFile?.also {
            val photoUri: Uri = FileProvider.getUriForFile(this, "$packageName.provider", it)
            takePictureLauncher.launch(photoUri)
        }
    }
}
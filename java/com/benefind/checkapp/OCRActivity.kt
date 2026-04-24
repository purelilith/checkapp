package com.benefind.checkapp

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        setContentView(R.layout.activity_ocr)

        backButtonOCR = findViewById(R.id.backButtonOCR)
        progressBar = findViewById(R.id.progressBar)
        engCaptureImgBtn = findViewById(R.id.engCaptureImgBtn)
        cameraImage = findViewById(R.id.cameraImage)
        captureImgBtn = findViewById(R.id.captureImgBtn)
        resultText = findViewById(R.id.resultText)
        takeCharBtn = findViewById(R.id.takeCharBtn)

        progressBar.visibility = View.GONE

        // разрешение на камеру
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) captureImage() else Toast.makeText(this, "Camera permission denied",
                    Toast.LENGTH_SHORT).show()
            }

        // лаунчер для фото с камеры
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                currentPhotoPath?.let { path ->
                    val bitmap = decodeSampledBitmapFromFile(path, 1024, 1024)
                    processImage(bitmap)
                }
            }
        }


        // лаунчер для выбора из галереи
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val bitmap = decodeSampledBitmapFromUri(it, 1024, 1024)
                processImage(bitmap)
            }
        }

        // кнопка включения камеры
        captureImgBtn.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        // кнопка открытия галереи
        engCaptureImgBtn.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        backButtonOCR.setOnClickListener {
            finish()
        }

        // кнопка расшифровки распозанного
        takeCharBtn.setOnClickListener {
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("recognizedText", recognizedAdditives)
            startActivity(intent)
        }
    }// основная функция распознавания через ML Kit
    private fun processImage(bitmap: Bitmap?) {
        if (bitmap == null) return
        (cameraImage.drawable as? BitmapDrawable)?.bitmap?.recycle()
        cameraImage.setImageBitmap(bitmap)
        progressBar.visibility = View.VISIBLE
        resultText.isEnabled = false

        val image = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                progressBar.visibility = View.GONE
                resultText.isEnabled = true

                // фильтр
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

    private fun getResizedBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        var width = bitmap.width
        var height = bitmap.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
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
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun decodeSampledBitmapFromFile(path: String, reqWidth: Int, reqHeight: Int): Bitmap {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, options)
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(path, options)
    }

    private fun decodeSampledBitmapFromUri(uri: Uri, reqWidth: Int, reqHeight: Int): Bitmap? {
        return contentResolver.openInputStream(uri)?.use { input ->
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(input, null, options)
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            options.inJustDecodeBounds = false
            contentResolver.openInputStream(uri)?.use { finalInput ->
                BitmapFactory.decodeStream(finalInput, null, options)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        recognizer.close()
        // Очищаем ImageView и удаляем bitmap из памяти
        (cameraImage.drawable as? BitmapDrawable)?.bitmap?.recycle()
        cameraImage.setImageDrawable(null)
    }
}
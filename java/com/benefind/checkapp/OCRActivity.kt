package com.benefind.checkapp
import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.content.pm.PackageManager
import java.util.Locale

import org.w3c.dom.Text
import java.util.Date
import com.google.mlkit.vision.text.TextRecognition

import android.text.method.ScrollingMovementMethod
import androidx.core.app.ActivityCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class OCRActivity : AppCompatActivity() {

    private lateinit var engCaptureImgBtn: Button
    private lateinit var cameraImage: ImageView
    private lateinit var captureImgBtn: Button
    private lateinit var resultText: TextView

    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var currentPhotoPath: String? = null
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>

    private lateinit var tessBaseAPI: TessBaseAPI
    private val tessDataPath: String by lazy { filesDir.absolutePath + "/" } // Путь к папке с tessdata


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr)

        engCaptureImgBtn = findViewById(R.id.engCaptureImgBtn)
        cameraImage = findViewById(R.id.cameraImage)
        captureImgBtn = findViewById(R.id.captureImgBtn)
        resultText = findViewById(R.id.resultText)

        copyTessDataFiles()

        tessBaseAPI = TessBaseAPI()
// Инициализируем Tesseract с русским языком (rus)
        if (!tessBaseAPI.init(tessDataPath, "rus")) {
            Toast.makeText(this, "Tesseract init failed", Toast.LENGTH_SHORT).show()
            finish()
        }

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                captureImage()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                currentPhotoPath?.let { path ->
                    val bitmap = BitmapFactory.decodeFile(path)
                    cameraImage.setImageBitmap(bitmap)
                    recognizeText(bitmap)
                }
            }
        }

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val inputStream = contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (bitmap != null) {
                    cameraImage.setImageBitmap(bitmap)
                    recognizeText(bitmap)
                } else {
                    Toast.makeText(this, "Не удалось загрузить изображение", Toast.LENGTH_SHORT).show()
                }
            }
        }
        val galleryBtn: Button = findViewById(R.id.engCaptureImgBtn)
        galleryBtn.setOnClickListener {
            // Запускаем выбор изображения без дополнительного запроса permission для чтения (для Android 13+ может потребоваться)
            pickImageLauncher.launch("image/*")
        }



        engCaptureImgBtn.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }


        captureImgBtn.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun copyTessDataFiles() {
        try {
            val tessDataFolder = File(tessDataPath + "tessdata/")
            if (!tessDataFolder.exists()) {
                tessDataFolder.mkdir()
            }

            val filePath = "$tessDataPath/tessdata/rus.traineddata"
            val file = File(filePath)

            if (!file.exists()) {
                assets.open("tessdata/rus.traineddata").use { inputStream ->
                    FileOutputStream(filePath).use { outputStream ->
                        val buffer = ByteArray(1024)
                        var read: Int
                        while (inputStream.read(buffer).also { read = it } != -1) {
                            outputStream.write(buffer, 0, read)
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка копирования данных tessdata: ${e.message}", Toast.LENGTH_LONG).show()
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
            Toast.makeText(this, "Error occured while creating the file", Toast.LENGTH_SHORT).show()
            null
        }
        photoFile?.also {
            val photoUri: Uri = FileProvider.getUriForFile(this, "$packageName.provider", it)
            takePictureLauncher.launch(photoUri)
        }
    }

    private fun recognizeText(bitmap: Bitmap) {
        tessBaseAPI.setImage(bitmap)
        val recognizedText = tessBaseAPI.utF8Text
        resultText.text = recognizedText
    }

    override fun onDestroy() {
        super.onDestroy()
        tessBaseAPI.end()
    }
}

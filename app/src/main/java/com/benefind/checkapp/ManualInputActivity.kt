package com.benefind.checkapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.benefind.checkapp.databinding.ActivityManualInputBinding

class ManualInputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManualInputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.parseColor("#EAEAEA")
        binding = ActivityManualInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.analyzeButton.setOnClickListener {
            val inputText = binding.compositionEditText.text.toString()

            if (inputText.isNotEmpty()) {
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("recognizedText", inputText)
                startActivity(intent)
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }
}
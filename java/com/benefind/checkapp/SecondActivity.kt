package com.benefind.checkapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        val button_to_main = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.buttonToMain)
        val button_to_OCR = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.buttonToOCR)
        val button_to_hand = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.buttonToHand)
        button_to_main.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        button_to_OCR.setOnClickListener {
            val intent = Intent(this, OCRActivity::class.java)
            startActivity(intent)
        }
        button_to_hand.setOnClickListener {
            val intent = Intent(this, ManualInputActivity::class.java)
            startActivity(intent)
        }

    }

}
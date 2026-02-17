package com.benefind.checkapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Button

class ResultActivity: AppCompatActivity() {
    private val res_list = mutableListOf(
        Additive("E-100", "Куркумин"),
        Additive("E-101", "Рибофлавин"),
        Additive("E-102", "Тартразин"),
        Additive("E-103", "Алканин"),
        Additive("E-104", "Желтый хеналиновый"),
        Additive("E-105", "Желтый прочный")
    )

    val resultText = findViewById<TextView>(R.id.resultText)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        findViewById<Button>(R.id.backButton).setOnClickListener {
            finish() // Закрывает текущее Activity и возвращает к предыдущему
        }

    }

}
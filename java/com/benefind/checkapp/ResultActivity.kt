package com.benefind.checkapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Button

class ResultActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        val backButton = findViewById<Button>(R.id.backButton)
        val textView = findViewById<TextView>(R.id.resultTextView)
        val receivedText = intent.getStringExtra("recognizedText") ?: ""

        val cleanedText = receivedText
            .uppercase()                                   // приводим к нижнему регистру (по желанию)
            .replace(Regex("[^\\w\\s]"), "")              // убираем всё кроме букв, цифр, пробелов и дефисов
            .trim()

        val wordList = cleanedText
            .split(Regex("\\s+"))                          // разбиваем по пробелам
            .filter { it.isNotEmpty() }                    // удаляем пустые элементы

        val matchedNames = DataProvider.res_list.filter { additive ->
            wordList.contains(additive.code)
        }.map { it.name }

        // Вывод названий добавок через запятую
        textView.text = if (matchedNames.isNotEmpty()) {
            matchedNames.joinToString(separator = ", ")
        } else {
            "Совпадений нет"
        }

        backButton.setOnClickListener {
            finish()
        }

    }
}
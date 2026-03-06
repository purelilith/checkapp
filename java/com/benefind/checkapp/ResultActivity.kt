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
            .uppercase()
            .replace(Regex("[^\\w\\s]"), "")
            .trim()

        val wordList = cleanedText
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() }

        val matchedNames = DataProvider.res_list.filter { additive ->
            wordList.contains(additive.code)
        }.map { additive ->
            "${additive.name} (${additive.code}) - ${additive.legality}" }

        // вывод названий добавок через запятую
        textView.text = if (matchedNames.isNotEmpty()) {
            matchedNames.joinToString(separator = "; ")
        } else {
            "Совпадений нет"
        }

        backButton.setOnClickListener {
            finish()
        }

    }
}
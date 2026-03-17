package com.benefind.checkapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Button
import android.widget.ImageButton
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        val textView = findViewById<TextView>(R.id.resultTextView)

        val receivedText = intent.getStringExtra("recognizedText") ?: ""
        val cleanedText = receivedText
            .uppercase()
            .replace(Regex("[^\\w\\s]"), "")
            .trim()

        val wordList = cleanedText
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() }

        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            val matchedAdditives = db.additiveDao().getByCodes(wordList)

            val resultString = if (matchedAdditives.isNotEmpty()) {
                matchedAdditives.joinToString(separator = "; ") { additive ->
                    "${additive.name} (${additive.code}) - ${additive.legality}"
                }
            } else {
                "Совпадений нет"
            }

            textView.text = resultString
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}
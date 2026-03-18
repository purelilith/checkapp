package com.benefind.checkapp

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Инициализация UI
        val backButton = findViewById<ImageButton>(R.id.backButton)
        val verdictCard = findViewById<CardView>(R.id.verdictCard)
        val verdictTitle = findViewById<TextView>(R.id.verdictTitle)
        val verdictSubtitle = findViewById<TextView>(R.id.verdictSubtitle)
        val recyclerView = findViewById<RecyclerView>(R.id.resultRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // 1. Получаем текст и разбиваем на слова (только буквы и цифры)
        val receivedText = intent.getStringExtra("recognizedText") ?: ""
        val wordList = receivedText.uppercase()
            .split(Regex("[^A-ZА-Я0-9]"))
            .filter { it.length > 2 }

        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            // 2. Поиск в базе данных Room
            val matchedItems = db.additiveDao().getByWords(wordList)

            if (matchedItems.isNotEmpty()) {
                // 3. Проверка: есть ли хоть одна запрещенная добавка?
                val isSafe = matchedItems.none { it.legality == "Запрещен" }

                if (isSafe) {
                    setVerdict(verdictCard, verdictTitle, verdictSubtitle,
                        "МОЖНО ЕСТЬ", "#3FB500", "#F1F8E9",
                        "Найдено ${matchedItems.size} безопасных совпадений")
                } else {
                    setVerdict(verdictCard, verdictTitle, verdictSubtitle,
                        "НЕЛЬЗЯ ЕСТЬ", "#D42C2C", "#FFEBEE",
                        "В составе обнаружены опасные добавки!")
                }

                // 4. Установка списка найденных ингредиентов
                recyclerView.adapter = ResultDetailAdapter(matchedItems)

            } else {
                // Если ничего не нашли в базе
                setVerdict(verdictCard, verdictTitle, verdictSubtitle,
                    "СОСТАВ ЧИСТ", "#757575", "#F5F5F5",
                    "Подозрительных добавок не обнаружено")
            }
        }

        backButton.setOnClickListener { finish() }
    }

    // Вспомогательная функция для настройки карточки вердикта
    private fun setVerdict(card: CardView, title: TextView, subtitle: TextView,
                           text: String, textColor: String, bgColor: String, subText: String) {
        title.text = text
        title.setTextColor(Color.parseColor(textColor))
        card.setCardBackgroundColor(Color.parseColor(bgColor))
        subtitle.text = subText
    }
}
package com.benefind.checkapp

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
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

        val backButton = findViewById<ImageButton>(R.id.backButton)
        val verdictCard = findViewById<CardView>(R.id.verdictCard)
        val verdictTitle = findViewById<TextView>(R.id.verdictTitle)
        val verdictSubtitle = findViewById<TextView>(R.id.verdictSubtitle)
        val recyclerView = findViewById<RecyclerView>(R.id.resultRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // получение распознанного текста
        val rawText = intent.getStringExtra("recognizedText")?.uppercase() ?: ""
        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            // загрузка всех ингредиентов из БД
            val allIngredients = db.additiveDao().getAll()

            // сверяю распозанное с ингредиентами из БД
            val matchedItems = allIngredients.filter { ingredient ->
                val nameMatch = rawText.contains(ingredient.name.uppercase())
                val codeMatch = ingredient.code?.let { rawText.contains(it.uppercase()) } ?: false
                nameMatch || codeMatch
            }

            if (matchedItems.isNotEmpty()) {
                // проверка безопасности
                val isSafe = matchedItems.none { it.legality == "Запрещен" || it.legality == "Опасно" }

                // определение типа заголовка
                val isCosmetic = matchedItems.any { it.category == "COSMETIC" }

                if (isSafe) {
                    val title = if (isCosmetic) "БЕЗОПАСНЫЙ СОСТАВ" else "МОЖНО ЕСТЬ"
                    setVerdict(verdictCard, verdictTitle, verdictSubtitle,
                        title, "#3FB500", "#F1F8E9",
                        "Найдено ${matchedItems.size} известных компонентов")
                } else {
                    val title = if (isCosmetic) "ЕСТЬ ОПАСНЫЕ КОМПОНЕНТЫ" else "НЕЛЬЗЯ ЕСТЬ"
                    setVerdict(verdictCard, verdictTitle, verdictSubtitle,
                        title, "#D42C2C", "#FFEBEE",
                        "Внимание! В составе обнаружены вредные вещества")
                }

                recyclerView.adapter = ResultDetailAdapter(matchedItems.toMutableList())

            } else {
                setVerdict(verdictCard, verdictTitle, verdictSubtitle,
                    "НЕ РАСПОЗНАНО", "#757575", "#F5F5F5",
                    "В базе пока нет данных об этих компонентах")
            }
        }

        backButton.setOnClickListener { finish() }
    }

    private fun setVerdict(card: CardView, title: TextView, subtitle: TextView,
                           text: String, textColor: String, bgColor: String, subText: String) {
        title.text = text
        title.setTextColor(Color.parseColor(textColor))
        card.setCardBackgroundColor(Color.parseColor(bgColor))
        subtitle.text = subText
    }
}
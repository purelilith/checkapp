package com.benefind.checkapp

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        val moreBtn = findViewById<ImageButton>(R.id.moreBtnSecond)
        val button_to_main =
            findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.buttonToMain)
        val button_to_OCR =
            findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.buttonToOCR)
        val button_to_hand =
            findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.buttonToHand)
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
        moreBtn.setOnClickListener { view ->
            // 1. Создаем само окошко
            val listPopupWindow = androidx.appcompat.widget.ListPopupWindow(this)

            // 2. Настраиваем данные (адаптер со шрифтом geologica)
            val items = listOf("Инструкция", "О приложении")
            val adapter = ArrayAdapter(this, R.layout.item_menu, R.id.menuItemText, items)

            listPopupWindow.setAdapter(adapter)
            listPopupWindow.anchorView = view // Привязываем к кнопке
            listPopupWindow.width = 600       // Ширина (подбери под дизайн)
            listPopupWindow.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.blocks_btns_style
                )
            )

            // --- ВОТ СЮДА ВСТАВЛЯЕМ ОБРАБОТЧИК КЛИКОВ ---
            listPopupWindow.setOnItemClickListener { _, _, position, _ ->
                when (position) {
                    0 -> {
                        showInstruction() // Твой метод для открытия инструкции
                    }

                    1 -> {
                        showAboutDialog() // Твой метод для "О приложении"
                    }
                }
                listPopupWindow.dismiss() // Закрыть меню после выбора
            }
            // --------------------------------------------

            // 3. Показываем меню
            listPopupWindow.show()
        }
    }

    private fun showInstruction() {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_instruction, null)

        view.findViewById<Button>(R.id.closeBtn).setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun showAboutDialog() {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_about, null)

        // Кнопка закрытия
        view.findViewById<Button>(R.id.closeAboutBtn).setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }
}
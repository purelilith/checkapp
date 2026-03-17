package com.benefind.checkapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.benefind.checkapp.databinding.ActivityAdditiveDetailBinding
import android.graphics.Color


class AdditiveDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdditiveDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdditiveDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем объект Additive из Intent
        // Важно: в Entity.kt добавь ': java.io.Serializable' к классу Additive
        val additive = intent.getSerializableExtra("EXTRA_ADDITIVE") as? Additive

        additive?.let {
            binding.detailCode.text = it.code
            binding.detailName.text = it.name
            binding.detailStatus.text = "Статус: ${it.legality}"

            // Красим статус
            if (it.legality == "Разрешен") {
                binding.detailStatus.setTextColor(Color.parseColor("#3fb500"))
            } else {
                binding.detailStatus.setTextColor(Color.parseColor("#d42c2c"))
            }

            // Если в базе есть описание, выводим его (или стандартный текст)
            binding.detailDescription.text = it.description
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }
}
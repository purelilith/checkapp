package com.benefind.checkapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.benefind.checkapp.databinding.ActivityAdditiveDetailBinding
import android.graphics.Color
import android.view.View

class AdditiveDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdditiveDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdditiveDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val additive = intent.getSerializableExtra("EXTRA_ADDITIVE") as? Additive

        additive?.let {
            binding.detailCode.text = it.name

            if (it.code.isNullOrEmpty()) {
                binding.detailName.visibility = View.GONE
            } else {
                binding.detailName.visibility = View.VISIBLE
                binding.detailName.text = it.code
            }

            binding.detailStatus.text = "Статус: ${it.legality}"

            val statusColor = when (it.legality) {
                "Разрешен", "Безопасен" -> "#3fb500"
                "Вреден" -> "#FF9800"
                else -> "#d42c2c"
            }
            binding.detailStatus.setTextColor(Color.parseColor(statusColor))

            binding.detailDescription.text = it.description
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }
}
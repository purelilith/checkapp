package com.benefind.checkapp

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.benefind.checkapp.databinding.ActivityMainBinding

data class Additive(val code: String, val name: String, val legality: String)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val list = mutableListOf(
        Additive("E100", "Куркумин", "Разрешен"),
        Additive("E101", "Рибофлавин", "Разрешен"),
        Additive("E102", "Тартразин", "Запрещен"),
        Additive("E103", "Алканин", "Запрещен"),
        Additive("E104", "Желтый хеналиновый", "Запрещен"),
        Additive("E105", "Желтый прочный", "Запрещен")
    )
    private val adapter = AdditiveAdapter(list.toMutableList())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.searchEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val filtered = list.filter {
                    it.code.contains(s.toString(), ignoreCase = true) ||
                            it.name.contains(s.toString(), ignoreCase = true)
                }
                adapter.updateList(filtered)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int){}
        })

        binding.backButton.setOnClickListener {
            finish()

        }

    }
}



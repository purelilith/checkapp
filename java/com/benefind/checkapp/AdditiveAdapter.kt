package com.benefind.checkapp

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.benefind.checkapp.databinding.ItemAdditiveBinding

class AdditiveAdapter(private var items: MutableList<Additive>) :
    RecyclerView.Adapter<AdditiveAdapter.AdditiveViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdditiveViewHolder {
        val binding = ItemAdditiveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdditiveViewHolder(binding)
    }

    inner class AdditiveViewHolder(val binding: ItemAdditiveBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: AdditiveViewHolder, position: Int) {
        val additive = items[position]
        val context = holder.itemView.context

        // 1. Новая градация безопасности (используем legality)
        // Добавляем "Вреден" для оранжевого статуса
        val statusColor = when (additive.legality) {
            "Безопасен", "Разрешен" -> Color.parseColor("#3FB500") // Зеленый
            "Вреден" -> Color.parseColor("#FF9800")               // Оранжевый
            else -> Color.parseColor("#D42C2C")                    // Красный (Опасен)
        }

        // 2. РАЗДЕЛЯЕМ ПРАВИЛА: КОСМЕТИКА VS ЕДА
        if (additive.category == "COSMETIC") {
            // --- ПРАВИЛА ДЛЯ КОСМЕТИКИ ---
            holder.binding.codeTextView.text = ""

            // Выбираем иконку из трех вариантов
            val iconRes = when (additive.legality) {
                "Безопасен"-> R.drawable.ic_circle_green
                "Вреден" -> R.drawable.ic_circle_orange
                else -> R.drawable.ic_circle_red
            }

            holder.binding.codeTextView.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(context, iconRes), null, null, null
            )
            holder.binding.codeTextView.visibility = View.VISIBLE

            // Для косметики название берем из name (так как code пустой)
            holder.binding.nameTextView.text = additive.name
            holder.binding.nameTextView.setTextColor(Color.BLACK)
        }
        else {
            // --- ПРАВИЛА ДЛЯ ЕДЫ (E-добавки) ---
            holder.binding.codeTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

            // Слева: ЦВЕТНОЙ КОД (поле .name)
            holder.binding.codeTextView.text = additive.name
            holder.binding.codeTextView.setTextColor(statusColor)
            holder.binding.codeTextView.visibility = View.VISIBLE

            // Справа: ЧЕРНОЕ НАЗВАНИЕ (поле .code)
            holder.binding.nameTextView.text = additive.code
            holder.binding.nameTextView.setTextColor(Color.BLACK)
        }

        // Обработка клика
        val clickListener = View.OnClickListener {
            val intent = Intent(context, AdditiveDetailActivity::class.java).apply {
                putExtra("EXTRA_ADDITIVE", additive)
            }
            context.startActivity(intent)
        }
        holder.binding.arrowImageView.setOnClickListener(clickListener)
    }

    fun updateList(newList: List<Additive>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}
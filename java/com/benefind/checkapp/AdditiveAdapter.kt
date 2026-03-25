package com.benefind.checkapp

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        // 1. Логика отображения кода (скрываем, если null или пустой)
        if (additive.code.isNullOrEmpty()) {
            holder.binding.codeTextView.visibility = View.GONE
        } else {
            holder.binding.codeTextView.visibility = View.VISIBLE
            holder.binding.codeTextView.text = additive.name
        }

        // 2. Установка названия
        holder.binding.nameTextView.text = additive.code

        // 3. Определение цвета на основе статуса
        val isSafe = additive.legality == "Разрешен" || additive.legality == "Безопасно"
        val statusColor = if (isSafe) Color.parseColor("#3FB500") else Color.parseColor("#D42C2C")

        // 4. Применяем цвет к коду, а если кода нет — к названию
        if (additive.code.isNullOrEmpty()) {
            holder.binding.nameTextView.setTextColor(statusColor)
        } else {
            holder.binding.codeTextView.setTextColor(statusColor)
            holder.binding.nameTextView.setTextColor(Color.BLACK)
        }

        // 5. Переход к деталям
        val clickListener = View.OnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AdditiveDetailActivity::class.java).apply {
                putExtra("EXTRA_ADDITIVE", additive)
            }
            context.startActivity(intent)
        }

        holder.binding.arrowImageView.setOnClickListener(clickListener)
        holder.itemView.setOnClickListener(clickListener) // Клик по всей карточке тоже работает
    }

    fun updateList(newList: List<Additive>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}
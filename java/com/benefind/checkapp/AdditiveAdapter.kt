package com.benefind.checkapp

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
        holder.binding.codeTextView.text = additive.code
        holder.binding.nameTextView.text = additive.name

        val color = if (additive.legality == "Разрешен") {
            Color.parseColor("#3fb500") // Зеленый
        } else {
            Color.parseColor("#d42c2c") // Красный
        }

        holder.binding.codeTextView.setTextColor(color)
    }

    fun updateList(newList: List<Additive>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}
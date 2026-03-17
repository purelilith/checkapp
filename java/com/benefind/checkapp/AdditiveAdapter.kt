package com.benefind.checkapp

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import kotlin.jvm.java
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
            Color.parseColor("#3fb500")
        } else {
            Color.parseColor("#d42c2c")
        }

        holder.binding.codeTextView.setTextColor(color)
        holder.binding.arrowImageView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AdditiveDetailActivity::class.java).apply {
                putExtra("EXTRA_ADDITIVE", additive)
            }
            context.startActivity(intent)
        }
    }

    fun updateList(newList: List<Additive>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    } }
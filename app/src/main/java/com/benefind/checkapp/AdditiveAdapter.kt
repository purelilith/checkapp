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

        val statusColor = when (additive.legality) {
            "Безопасен", "Разрешен" -> Color.parseColor("#3FB500") // зеленый
            "Вреден" -> Color.parseColor("#FF9800")               // оранжевый
            else -> Color.parseColor("#D42C2C")                    // красный
        }

        if (additive.category == "COSMETIC") {
            holder.binding.codeTextView.text = ""

            val iconRes = when (additive.legality) {
                "Безопасен"-> R.drawable.ic_circle_green
                "Вреден" -> R.drawable.ic_circle_orange
                else -> R.drawable.ic_circle_red
            }

            holder.binding.codeTextView.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(context, iconRes), null, null, null
            )
            holder.binding.codeTextView.visibility = View.VISIBLE

            holder.binding.nameTextView.text = additive.name
            holder.binding.nameTextView.setTextColor(Color.BLACK)
        }
        else {
            holder.binding.codeTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

            holder.binding.codeTextView.text = additive.name
            holder.binding.codeTextView.setTextColor(statusColor)
            holder.binding.codeTextView.visibility = View.VISIBLE

            holder.binding.nameTextView.text = additive.code
            holder.binding.nameTextView.setTextColor(Color.BLACK)
        }
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
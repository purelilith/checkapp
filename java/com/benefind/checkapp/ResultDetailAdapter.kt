package com.benefind.checkapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ResultDetailAdapter(private val items: List<Additive>) :
    RecyclerView.Adapter<ResultDetailAdapter.DetailViewHolder>() {

    class DetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val resCode: TextView = view.findViewById(R.id.resCode)
        val resName: TextView = view.findViewById(R.id.resName)
        val resLegality: TextView = view.findViewById(R.id.resLegality)
        val resDescription: TextView = view.findViewById(R.id.resDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_result_detail, parent, false)
        return DetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val item = items[position]

        holder.resCode.text = item.code
        holder.resName.text = item.name
        holder.resLegality.text = item.legality
        holder.resDescription.text = item.description

        // Красим элементы в зависимости от статуса безопасности
        val color = if (item.legality == "Запрещен") {
            Color.parseColor("#D42C2C") // Красный
        } else {
            Color.parseColor("#3FB500") // Зеленый
        }

        holder.resCode.setTextColor(color)
        holder.resLegality.setTextColor(color)
    }

    override fun getItemCount() = items.size
}
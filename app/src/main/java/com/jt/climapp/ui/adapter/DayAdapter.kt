package com.jt.climapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jt.climapp.R
import com.jt.climapp.ui.data.model.Daily

class DayAdapter (private val days: List<Daily>) : RecyclerView.Adapter<DayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return DayViewHolder(layoutInflater.inflate(R.layout.item_day, parent, false))
    }
    override fun getItemCount(): Int = days.size
    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val item = days[position]
        holder.bind(item)
    }
}